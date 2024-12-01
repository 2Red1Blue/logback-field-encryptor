package com.laiu.log;


import com.laiu.log.config.FieldEncryptConfig;
import com.laiu.log.encrypt.Base64Encrypt;
import com.laiu.log.encrypt.EncryptStrategy;
import com.laiu.log.encrypt.XXTeaEncrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author liuzhixin
 * @Description
 */
public class FieldEncryptService {
    private static final Logger logger = LoggerFactory.getLogger(FieldEncryptService.class);
    private FieldEncryptConfig config;
    private Map<String, List<Pattern>> fieldPatterns = new HashMap<>();
    private Map<String, EncryptStrategy> encryptStrategies = new HashMap<>();

    private FieldEncryptService() {
        config = loadConfig();
        // 为每个字段编译正则并创建加密策略
        config.getEncryptFields().forEach((fieldName, field) -> {
            // 编译正则
            List<Pattern> patterns = field.getPatterns().stream()
                    .map(pattern -> Pattern.compile(String.format(pattern, fieldName)))
                    .collect(Collectors.toList());
            fieldPatterns.put(fieldName, patterns);

            // 创建加密策略
            EncryptStrategy strategy = "XXTEA".equals(field.getEncryptType())
                    ? new XXTeaEncrypt(field.getEncryptKey())
                    : new Base64Encrypt();
            encryptStrategies.put(fieldName, strategy);
        });
    }

    private static class FieldEncryptServiceHolder {
        private static final FieldEncryptService INSTANCE = new FieldEncryptService();
    }

    public static FieldEncryptService getInstance() {
        return FieldEncryptServiceHolder.INSTANCE;
    }

    public String encryptMessage(String message) {
        try {
            // 对每个字段进行加密
            for (Map.Entry<String, List<Pattern>> entry : fieldPatterns.entrySet()) {
                String fieldName = entry.getKey();
                if (message.contains(fieldName)) {
                    for (Pattern pattern : entry.getValue()) {
                        try {
                            message = encryptField(message, pattern, fieldName);
                        } catch (Exception e) {
                            message = handleEncryptionError(message, fieldName, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            String errMessage = String.format("ENCRYPTION_ERROR: General encryption failed. Error: %s, Stack: %s",
                    e.getMessage(),
                    getStackTraceAsString(e));
            logger.warn(errMessage);
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
            if (asStream != null) {
                InputStreamReader reader = new InputStreamReader(asStream, StandardCharsets.UTF_8);
                props.load(reader);
                return loadFromProperties(props);
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

    public String encryptMessageWithPatterns(String message, Map<String, List<Pattern>> patterns) {
        try {
            // 对每个字段进行加密
            for (Map.Entry<String, List<Pattern>> entry : patterns.entrySet()) {
                String fieldName = entry.getKey();
                if (message.contains(fieldName)) {
                    for (Pattern pattern : entry.getValue()) {
                        try {
                            message = encryptField(message, pattern, fieldName);
                        } catch (Exception e) {
                            message = handleEncryptionError(message, fieldName, e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            String errMessage = String.format("ENCRYPTION_ERROR: General encryption failed. Error: %s, Stack: %s",
                    e.getMessage(),
                    getStackTraceAsString(e));
            logger.warn(errMessage);
        }
        return message;
    }
}
