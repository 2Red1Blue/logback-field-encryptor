package com.laiu.log.convertor;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.laiu.log.util.XXTEAUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liuzhixin
 * @Description:
 */


public class MobileEncryptConverter extends ClassicConverter {

    /*
     * XXTEAUtil加解密使用的key
     * */
    public static final String XXTEAUtil_KEY = "a$fHDF&G;lNFj%ea";
    private static final Pattern MOBILE_PATTERN = Pattern.compile("(mobile:)(\\d+)");
    private static final Pattern JSON_MOBILE_PATTERN = Pattern.compile("(\"mobile\":\")(\\d+)(\")");
    private static final Pattern TO_STRING_MOBILE_PATTERN = Pattern.compile("(mobile=)(\\d+)");


    @Override
    public String convert(ILoggingEvent event) {
        String message = event.getFormattedMessage();
        try {
            if (message.contains("mobile:")) {
                // 加密普通文本中的 mobile 字段
                message = encryptMobileField(message, MOBILE_PATTERN);
            } else if (message.contains("\"mobile\":")) {
                // 加密 JSON 格式中的 mobile 字段
                message = encryptMobileField(message, JSON_MOBILE_PATTERN);
            } else if (message.contains("mobile=")) {
                // 加密 toString 格式中的 mobile 字段
                message = encryptMobileField(message, TO_STRING_MOBILE_PATTERN);
            }
        } catch (Exception e) {
            // 处理加密异常
            return "ENCRYPTION_ERROR";
        }
        return message;
    }

    private String encryptMobileField(String message, Pattern pattern) {
        Matcher matcher = pattern.matcher(message);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String encryptedMobile = XXTEAUtil.encryptToBase64String(matcher.group(2), XXTEAUtil_KEY);
            matcher.appendReplacement(sb, matcher.group(1) + encryptedMobile + (matcher.groupCount() == 3 ? matcher.group(3) : ""));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}