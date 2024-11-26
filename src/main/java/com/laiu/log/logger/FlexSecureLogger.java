package com.laiu.log.logger;


import com.laiu.log.FieldEncryptService;
import com.laiu.log.config.FieldEncryptConfig;
import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 修复日志定位问题
 * @author liuzhixin
 * @Description
 */
public class FlexSecureLogger implements org.slf4j.Logger {
    private final LocationAwareLogger log;
    private final FieldEncryptService fieldEncryptService;
    private static final String FQCN = FlexSecureLogger.class.getName();

    public FlexSecureLogger(org.slf4j.Logger slfLogger) {
        this.log = (LocationAwareLogger) slfLogger;
        this.fieldEncryptService = FieldEncryptService.getInstance();
    }

    private void logWithEncryption(int logLevel, String message, Collection<String> fieldsToEncrypt, Object... args) {
        FormattingTuple tuple = MessageFormatter.arrayFormat(message, args);
        String formattedMessage = tuple.getMessage();
        Throwable throwable = tuple.getThrowable();
        String encryptedMessage = encryptSpecificFields(formattedMessage, fieldsToEncrypt);

        switch (logLevel) {
            case LogLevel.TRACE:
                log.log(null, FQCN, LocationAwareLogger.TRACE_INT, encryptedMessage, null, throwable);
                break;
            case LogLevel.DEBUG:
                log.log(null, FQCN, LocationAwareLogger.DEBUG_INT, encryptedMessage, null, throwable);
                break;
            case LogLevel.INFO:
                log.log(null, FQCN, LocationAwareLogger.INFO_INT, encryptedMessage, null, throwable);
                break;
            case LogLevel.WARN:
                log.log(null, FQCN, LocationAwareLogger.WARN_INT, encryptedMessage, null, throwable);
                break;
            case LogLevel.ERROR:
                log.log(null, FQCN, LocationAwareLogger.ERROR_INT, encryptedMessage, null, throwable);
                break;
            default:
                log.log(null, FQCN, LocationAwareLogger.INFO_INT, encryptedMessage, null, throwable);
        }
    }

    public void infoWithEncryption(String message, Collection<String> fieldsToEncrypt, Object... args) {
        logWithEncryption(LogLevel.INFO, message, fieldsToEncrypt, args);
    }

    public void infoWithEncryption(String message, Collection<String> fieldsToEncrypt) {
        logWithEncryption(LogLevel.INFO, message, fieldsToEncrypt);
    }

    public void errorWithEncryption(String message, Collection<String> fieldsToEncrypt, Object... args) {
        logWithEncryption(LogLevel.ERROR, message, fieldsToEncrypt, args);
    }

    public void errorWithEncryption(String message, Collection<String> fieldsToEncrypt, Object o) {
        String formattedMessage = String.format(message, o);
        logWithEncryption(LogLevel.ERROR, formattedMessage, fieldsToEncrypt);
    }

    public void errorWithEncryption(String message, Collection<String> fieldsToEncrypt) {
        logWithEncryption(LogLevel.ERROR, message, fieldsToEncrypt);
    }

    public void errorWithEncryption(String message, Collection<String> fieldsToEncrypt, Throwable throwable) {
        logWithEncryption(LogLevel.ERROR, message, fieldsToEncrypt, throwable);
    }

    public void errorWithEncryption(String message, Collection<String> fieldsToEncrypt, Throwable t, Object... args) {
        logWithEncryption(LogLevel.ERROR, message, fieldsToEncrypt, args);
    }

    public void warnWithEncryption(String message, Collection<String> fieldsToEncrypt, Throwable throwable) {
        logWithEncryption(LogLevel.WARN, message, fieldsToEncrypt, throwable);
    }

    public void warnWithEncryption(String message, Collection<String> fieldsToEncrypt) {
        logWithEncryption(LogLevel.WARN, message, fieldsToEncrypt);
    }

    public void warnWithEncryption(String message, Collection<String> fieldsToEncrypt, Object... args) {
        logWithEncryption(LogLevel.WARN, message, fieldsToEncrypt, args);
    }

    public void warnWithEncryption(String message, Collection<String> fieldsToEncrypt, Throwable t, Object... args) {
        logWithEncryption(LogLevel.WARN, message, fieldsToEncrypt, args);
    }

    public void debugWithEncryption(String message, Collection<String> fieldsToEncrypt, Object... args) {
        logWithEncryption(LogLevel.DEBUG, message, fieldsToEncrypt, args);
    }

    public void debugWithEncryption(String message, Collection<String> fieldsToEncrypt) {
        logWithEncryption(LogLevel.DEBUG, message, fieldsToEncrypt);
    }

    private String encryptSpecificFields(String message, Collection<String> fieldsToEncrypt) {
        if (fieldsToEncrypt == null || fieldsToEncrypt.isEmpty() || message == null) {
            return message;
        }

        try {
            Map<String, List<Pattern>> tempFieldPatterns = fieldsToEncrypt.stream()
                    .collect(Collectors.toMap(
                            fieldName -> fieldName,
                            fieldName -> Collections.singletonList(Pattern.compile(String.format(FieldEncryptConfig.DEFAULT_PATTERN, fieldName)))
                    ));

            return fieldEncryptService.encryptMessageWithPatterns(message, tempFieldPatterns);
        } catch (Exception e) {
            log.warn("Failed to encrypt message,cause:{}", e.getMessage(), e);
            return message;
        }
    }

    @Override
    public String getName() {
        return log.getName();
    }

    @Override
    public boolean isTraceEnabled() {
        return log.isTraceEnabled();
    }

    @Override
    public void trace(String s) {
        log.trace(s);
    }

    @Override
    public void trace(String s, Object o) {
        log.trace(s, o);
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        log.trace(s, o, o1);
    }

    @Override
    public void trace(String s, Object... objects) {
        log.trace(s, objects);
    }

    @Override
    public void trace(String s, Throwable throwable) {
        log.trace(s, throwable);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return log.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String s) {
        log.trace(marker, s);
    }

    @Override
    public void trace(Marker marker, String s, Object o) {
        log.trace(marker, s, o);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {
        log.trace(marker, s, o, o1);
    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {
        log.trace(marker, s, objects);
    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {
        log.trace(marker, s, throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public void debug(String s) {
        log.debug(s);
    }

    @Override
    public void debug(String s, Object o) {
        log.debug(s, o);
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        log.debug(s, o, o1);
    }

    @Override
    public void debug(String s, Object... objects) {
        log.debug(s, objects);
    }

    @Override
    public void debug(String s, Throwable throwable) {
        log.debug(s, throwable);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return log.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String s) {
        log.debug(marker, s);
    }

    @Override
    public void debug(Marker marker, String s, Object o) {
        log.debug(marker, s, o);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {
        log.debug(marker, s, o, o1);
    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {
        log.debug(marker, s, objects);
    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {
        log.debug(marker, s, throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    @Override
    public void info(String s) {
        log.info(s);
    }

    @Override
    public void info(String s, Object o) {
        log.info(s, o);
    }

    @Override
    public void info(String s, Object o, Object o1) {
        log.info(s, o, o1);
    }

    @Override
    public void info(String s, Object... objects) {
        log.info(s, objects);
    }

    @Override
    public void info(String s, Throwable throwable) {
        log.info(s, throwable);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return log.isInfoEnabled(marker);
    }

    @Override
    public void info(Marker marker, String s) {
        log.info(marker, s);
    }

    @Override
    public void info(Marker marker, String s, Object o) {
        log.info(marker, s, o);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {
        log.info(marker, s, o, o1);
    }

    @Override
    public void info(Marker marker, String s, Object... objects) {
        log.info(marker, s, objects);
    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {
        log.info(marker, s, throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    @Override
    public void warn(String s) {
        log.warn(s);
    }

    @Override
    public void warn(String s, Object o) {
        log.warn(s, o);
    }

    @Override
    public void warn(String s, Object... objects) {
        log.warn(s, objects);
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        log.warn(s, o, o1);
    }

    @Override
    public void warn(String s, Throwable throwable) {
        log.warn(s, throwable);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return log.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String s) {
        log.warn(marker, s);
    }

    @Override
    public void warn(Marker marker, String s, Object o) {
        log.warn(marker, s, o);
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {
        log.warn(marker, s, o, o1);
    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {
        log.warn(marker, s, objects);
    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {
        log.warn(marker, s, throwable);
    }

    @Override
    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    @Override
    public void error(String s) {
        log.error(s);
    }

    @Override
    public void error(String s, Object o) {
        log.error(s, o);
    }

    @Override
    public void error(String s, Object o, Object o1) {
        log.error(s, o, o1);
    }

    @Override
    public void error(String s, Object... objects) {
        log.error(s, objects);
    }

    @Override
    public void error(String s, Throwable throwable) {
        log.error(s, throwable);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return log.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String s) {
        log.error(marker, s);
    }

    @Override
    public void error(Marker marker, String s, Object o) {
        log.error(marker, s, o);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {
        log.error(marker, s, o, o1);
    }

    @Override
    public void error(Marker marker, String s, Object... objects) {
        log.error(marker, s, objects);
    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {
        log.error(marker, s, throwable);
    }

    static class LogLevel {
        public static final int TRACE = 0;
        public static final int DEBUG = 1;
        public static final int INFO = 2;
        public static final int WARN = 3;
        public static final int ERROR = 4;
    }
}