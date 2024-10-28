package com.yuanbao.sms.convertor;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.yuanbao.sms.spi.EncryptStrategyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yuanbao.sms.config.FieldEncryptConfig;
import com.yuanbao.sms.encrypt.Base64Encrypt;
import com.yuanbao.sms.encrypt.EncryptStrategy;
import com.yuanbao.sms.encrypt.XXTeaEncrypt;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * @author liuzhixin
 * @Description:
 */
public class FieldEncryptConverter extends ClassicConverter {
    private static final Logger logger = LoggerFactory.getLogger(FieldEncryptConverter.class);
    private FieldEncryptConfig config;
    private Map<String, List<Pattern>> fieldPatterns = new HashMap<>();
    private Map<String, EncryptStrategy> encryptStrategies = new HashMap<>();


    @Override
    public void start() {
        config = loadConfig();
        // 为每个字段编译正则并创建加密策略
        config.getEncryptFields().forEach((fieldName, field) -> {
            try {
                // 编译正则
                List<Pattern> patterns = field.getPatterns().stream()
                        .map(pattern -> Pattern.compile(String.format(pattern, fieldName)))
                        .collect(Collectors.toList());
                fieldPatterns.put(fieldName, patterns);

                // 创建加密策略
                EncryptStrategy strategy = EncryptStrategyFactory.createStrategy(field.getEncryptType(), field.getEncryptKey());

                encryptStrategies.put(fieldName, strategy);
            } catch (Exception e) {
                logger.warn("Failed to compile pattern for field: {}, not use encrypt, error: {}", fieldName, e.getMessage(), e);
            }
        });
        super.start();
    }

    @Override
    public String convert(ILoggingEvent event) {
        String message = event.getFormattedMessage();

            // 对每个字段进行加密
            for (Map.Entry<String, List<Pattern>> entry : fieldPatterns.entrySet()) {
                String fieldName = entry.getKey();
                if (message.contains(fieldName)) {
                    for (Pattern pattern : entry.getValue()) {
                        try {
                            message = encryptField(message, pattern, fieldName);
                        } catch (Exception e) {
                            //message = handleEncryptionError(message, fieldName, e);
                            logger.warn("Failed to encrypt field: {}, error: {}", fieldName, e.getMessage(), e);
                        }
                    }
                }
            }
        return message;
    }
    private String handleEncryptionError(String message, String fieldName, Exception e) {
        String errorMsg = String.format("[ENCRYPTION_ERROR for %s: %s]", fieldName, e.getMessage());
        return message.replaceAll(
                String.format("(%s:)[^,}\\s]+", fieldName),
                "$1" + errorMsg
        );
    }
    private String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    /*private String encryptField(String message, Pattern pattern, String fieldName) {
        Matcher matcher = pattern.matcher(message);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String value = matcher.group(2);
            if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim())) {
                // 保持原样输出
                matcher.appendReplacement(sb, matcher.group(1) + value +
                        (matcher.groupCount() == 3 ? matcher.group(3) : ""));
                continue;
            }
            String encrypted = encryptStrategies.get(fieldName).encrypt(value);
            matcher.appendReplacement(sb, matcher.group(1) + encrypted +
                    (matcher.groupCount() == 3 ? matcher.group(3) : ""));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }*/
    //todo:这个方法是针对特定正则组的,若使用方提供匹配规则需spi
    //"(\\\\*\"*%s\\\\*\"*\\s*[:=]\\s*)(\\\\*\"*)(.*?)(?=,|\\}|\\)|$"
    private String encryptField(String message, Pattern pattern, String fieldName) {
        Matcher matcher = pattern.matcher(message);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String replacement;
            int count = matcher.groupCount();
            if (count >= 3) {
                if (matcher.group(1) == null) {
                    continue;
                }
                String value = matcher.group(3).replaceAll("[\\\\\"]+$", "");
                String encrypted = encryptStrategies.get(fieldName).encrypt(value);
                String replacement1 = matcher.group(1) + matcher.group(2) + encrypted +
                        matcher.group(2);
                replacement = replacement1.replace("\\", "\\\\");
            } else {
                String encrypt = encryptStrategies.get(fieldName).encrypt(matcher.group(2));
                replacement = matcher.group(1) + encrypt +
                        (matcher.groupCount() == 3 ? matcher.group(3) : "");
            }
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private FieldEncryptConfig loadConfig() {
        FieldEncryptConfig config = new FieldEncryptConfig();
        try {
            Properties props = new Properties();
            InputStream asStream = getClass().getClassLoader().getResourceAsStream("field-encrypt.properties");
            if (asStream != null){
                InputStreamReader reader = new InputStreamReader(asStream, StandardCharsets.UTF_8);
                props.load(reader);
                return  loadFromProperties(props);
            } else {
                logger.warn("No configuration file found, using default configuration");
                throw new RuntimeException("No configuration file found");
            }

        } catch (Exception e) {
            String errorMsg = String.format("Failed to load encryption configuration: %s. Using default configuration.",
                    e.getMessage());
            logger.warn(errorMsg, e);
            // 使用默认配置(兼容没有配置文件)
            config.addField("mobile", "XXTEA", "a$fHDF&G;lNFj%ea");
        }
        return config;
    }

    private FieldEncryptConfig loadFromProperties(Properties props) {
        FieldEncryptConfig config = new FieldEncryptConfig();

        // 读取字段列表
        String[] fields = props.getProperty("encrypt.fields", "mobile").split(",");

        for (String field : fields) {
            String fieldName = field.trim();
            String encryptType = props.getProperty(fieldName + ".encrypt.type", "XXTEA");
            String encryptKey = props.getProperty(fieldName + ".encrypt.key", "a$fHDF&G;lNFj%ea");

            // 读取自定义匹配模式（如果有）
            List<String> patterns = null;
            String patternsStr = props.getProperty(fieldName + ".patterns");
            if (patternsStr != null && !patternsStr.isEmpty()) {
                // 分割模式并去除空白字符
                patterns = Arrays.stream(patternsStr.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            }

            config.addField(fieldName, encryptType, encryptKey, patterns);
        }

        return config;
    }

}
