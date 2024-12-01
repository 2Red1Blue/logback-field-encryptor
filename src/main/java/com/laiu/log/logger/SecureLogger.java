package com.laiu.log.logger;

import com.laiu.log.FieldEncryptService;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

/**
 * 可正确定位日志的加密增强logger
 * @author liuzhixin
 */
public class SecureLogger implements Logger {
    private final Logger slfLogger;
    private final FieldEncryptService fieldEncryptService;
    private final boolean isLocationAware;
    private static final String FQCN = SecureLogger.class.getName();

    public SecureLogger(Logger slfLogger) {
        this.slfLogger = slfLogger;
        this.fieldEncryptService = FieldEncryptService.getInstance();
        this.isLocationAware = slfLogger instanceof LocationAwareLogger;
    }

    /**
     * 自定义方法实现加密逻辑和日志定位
     */
    private void log(int level, String msg, Object[] args, Throwable throwable) {
        String formattedMessage;
        if (args != null && args.length > 0) {
            FormattingTuple ft = MessageFormatter.arrayFormat(msg, args);
            formattedMessage = fieldEncryptService.encryptMessage(ft.getMessage());
            // If there was a throwable in the args, use it
            throwable = throwable == null ? ft.getThrowable() : throwable;
        } else {
            formattedMessage = fieldEncryptService.encryptMessage(msg);
        }

        if (isLocationAware) {
            LocationAwareLogger locationAwareLogger = (LocationAwareLogger) slfLogger;
            locationAwareLogger.log(null, FQCN, level, formattedMessage, null, throwable);
        } else {
            switch (level) {
                case LocationAwareLogger.TRACE_INT:
                    slfLogger.trace(formattedMessage, throwable);
                    break;
                case LocationAwareLogger.DEBUG_INT:
                    slfLogger.debug(formattedMessage, throwable);
                    break;
                case LocationAwareLogger.INFO_INT:
                    slfLogger.info(formattedMessage, throwable);
                    break;
                case LocationAwareLogger.WARN_INT:
                    slfLogger.warn(formattedMessage, throwable);
                    break;
                case LocationAwareLogger.ERROR_INT:
                    slfLogger.error(formattedMessage, throwable);
                    break;
            }
        }
    }

    /**
     * 带有Marker参数的自定义方法
     */
    private void log(Marker marker, int level, String msg, Object[] args, Throwable throwable) {
        String formattedMessage;
        if (args != null && args.length > 0) {
            FormattingTuple ft = MessageFormatter.arrayFormat(msg, args);
            formattedMessage = fieldEncryptService.encryptMessage(ft.getMessage());
            throwable = throwable == null ? ft.getThrowable() : throwable;
        } else {
            formattedMessage = fieldEncryptService.encryptMessage(msg);
        }

        if (isLocationAware) {
            LocationAwareLogger locationAwareLogger = (LocationAwareLogger) slfLogger;
            locationAwareLogger.log(marker, FQCN, level, formattedMessage, null, throwable);
        } else {
            // Fallback for marker-based logging
            switch (level) {
                case LocationAwareLogger.TRACE_INT:
                    slfLogger.trace(marker, formattedMessage, throwable);
                    break;
                case LocationAwareLogger.DEBUG_INT:
                    slfLogger.debug(marker, formattedMessage, throwable);
                    break;
                case LocationAwareLogger.INFO_INT:
                    slfLogger.info(marker, formattedMessage, throwable);
                    break;
                case LocationAwareLogger.WARN_INT:
                    slfLogger.warn(marker, formattedMessage, throwable);
                    break;
                case LocationAwareLogger.ERROR_INT:
                    slfLogger.error(marker, formattedMessage, throwable);
                    break;
            }
        }
    }

    @Override
    public String getName() {
        return slfLogger.getName();
    }


    @Override
    public boolean isTraceEnabled() {
        return slfLogger.isTraceEnabled();
    }

    @Override
    public void trace(String msg) {
        if (isTraceEnabled()) {
            log(LocationAwareLogger.TRACE_INT, msg, null, null);
        }
    }

    @Override
    public void trace(String format, Object arg) {
        if (isTraceEnabled()) {
            log(LocationAwareLogger.TRACE_INT, format, new Object[]{arg}, null);
        }
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (isTraceEnabled()) {
            log(LocationAwareLogger.TRACE_INT, format, new Object[]{arg1, arg2}, null);
        }
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (isTraceEnabled()) {
            log(LocationAwareLogger.TRACE_INT, format, arguments, null);
        }
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (isTraceEnabled()) {
            log(LocationAwareLogger.TRACE_INT, msg, null, t);
        }
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return slfLogger.isTraceEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String msg) {
        if (isTraceEnabled(marker)) {
            log(marker, LocationAwareLogger.TRACE_INT, msg, null, null);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        if (isTraceEnabled(marker)) {
            log(marker, LocationAwareLogger.TRACE_INT, format, new Object[]{arg}, null);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        if (isTraceEnabled(marker)) {
            log(marker, LocationAwareLogger.TRACE_INT, format, new Object[]{arg1, arg2}, null);
        }
    }

    @Override
    public void trace(Marker marker, String format, Object... arguments) {
        if (isTraceEnabled(marker)) {
            log(marker, LocationAwareLogger.TRACE_INT, format, arguments, null);
        }
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        if (isTraceEnabled(marker)) {
            log(marker, LocationAwareLogger.TRACE_INT, msg, null, t);
        }
    }

    // DEBUG level implementations
    @Override
    public boolean isDebugEnabled() {
        return slfLogger.isDebugEnabled();
    }

    @Override
    public void debug(String msg) {
        if (isDebugEnabled()) {
            log(LocationAwareLogger.DEBUG_INT, msg, null, null);
        }
    }

    @Override
    public void debug(String format, Object arg) {
        if (isDebugEnabled()) {
            log(LocationAwareLogger.DEBUG_INT, format, new Object[]{arg}, null);
        }
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (isDebugEnabled()) {
            log(LocationAwareLogger.DEBUG_INT, format, new Object[]{arg1, arg2}, null);
        }
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (isDebugEnabled()) {
            log(LocationAwareLogger.DEBUG_INT, format, arguments, null);
        }
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (isDebugEnabled()) {
            log(LocationAwareLogger.DEBUG_INT, msg, null, t);
        }
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return slfLogger.isDebugEnabled(marker);
    }

    @Override
    public void debug(Marker marker, String msg) {
        if (isDebugEnabled(marker)) {
            log(marker, LocationAwareLogger.DEBUG_INT, msg, null, null);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        if (isDebugEnabled(marker)) {
            log(marker, LocationAwareLogger.DEBUG_INT, format, new Object[]{arg}, null);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if (isDebugEnabled(marker)) {
            log(marker, LocationAwareLogger.DEBUG_INT, format, new Object[]{arg1, arg2}, null);
        }
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        if (isDebugEnabled(marker)) {
            log(marker, LocationAwareLogger.DEBUG_INT, format, arguments, null);
        }
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        if (isDebugEnabled(marker)) {
            log(marker, LocationAwareLogger.DEBUG_INT, msg, null, t);
        }
    }

    // INFO level implementations
    @Override
    public boolean isInfoEnabled() {
        return slfLogger.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        if (isInfoEnabled()) {
            log(LocationAwareLogger.INFO_INT, msg, null, null);
        }
    }

    @Override
    public void info(String format, Object arg) {
        if (isInfoEnabled()) {
            log(LocationAwareLogger.INFO_INT, format, new Object[]{arg}, null);
        }
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (isInfoEnabled()) {
            log(LocationAwareLogger.INFO_INT, format, new Object[]{arg1, arg2}, null);
        }
    }

    @Override
    public void info(String format, Object... arguments) {
        if (isInfoEnabled()) {
            log(LocationAwareLogger.INFO_INT, format, arguments, null);
        }
    }

    @Override
    public void info(String msg, Throwable t) {
        if (isInfoEnabled()) {
            log(LocationAwareLogger.INFO_INT, msg, null, t);
        }
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return slfLogger.isInfoEnabled(marker);
    }

    @Override
    public void info(Marker marker, String msg) {
        if (isInfoEnabled(marker)) {
            log(marker, LocationAwareLogger.INFO_INT, msg, null, null);
        }
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        if (isInfoEnabled(marker)) {
            log(marker, LocationAwareLogger.INFO_INT, format, new Object[]{arg}, null);
        }
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        if (isInfoEnabled(marker)) {
            log(marker, LocationAwareLogger.INFO_INT, format, new Object[]{arg1, arg2}, null);
        }
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        if (isInfoEnabled(marker)) {
            log(marker, LocationAwareLogger.INFO_INT, format, arguments, null);
        }
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        if (isInfoEnabled(marker)) {
            log(marker, LocationAwareLogger.INFO_INT, msg, null, t);
        }
    }


    @Override
    public boolean isWarnEnabled() {
        return slfLogger.isWarnEnabled();
    }

    @Override
    public void warn(String msg) {
        if (isWarnEnabled()) {
            log(LocationAwareLogger.WARN_INT, msg, null, null);
        }
    }

    @Override
    public void warn(String format, Object arg) {
        if (isWarnEnabled()) {
            log(LocationAwareLogger.WARN_INT, format, new Object[]{arg}, null);
        }
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (isWarnEnabled()) {
            log(LocationAwareLogger.WARN_INT, format, arguments, null);
        }
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (isWarnEnabled()) {
            log(LocationAwareLogger.WARN_INT, format, new Object[]{arg1, arg2}, null);
        }
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (isWarnEnabled()) {
            log(LocationAwareLogger.WARN_INT, msg, null, t);
        }
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return slfLogger.isWarnEnabled(marker);
    }

    @Override
    public void warn(Marker marker, String msg) {
        if (isWarnEnabled(marker)) {
            log(marker, LocationAwareLogger.WARN_INT, msg, null, null);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        if (isWarnEnabled(marker)) {
            log(marker, LocationAwareLogger.WARN_INT, format, new Object[]{arg}, null);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        if (isWarnEnabled(marker)) {
            log(marker, LocationAwareLogger.WARN_INT, format, new Object[]{arg1, arg2}, null);
        }
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        if (isWarnEnabled(marker)) {
            log(marker, LocationAwareLogger.WARN_INT, format, arguments, null);
        }
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        if (isWarnEnabled(marker)) {
            log(marker, LocationAwareLogger.WARN_INT, msg, null, t);
        }
    }

    @Override
    public boolean isErrorEnabled() {
        return slfLogger.isErrorEnabled();
    }

    @Override
    public void error(String msg) {
        if (isErrorEnabled()) {
            log(LocationAwareLogger.ERROR_INT, msg, null, null);
        }
    }

    @Override
    public void error(String format, Object arg) {
        if (isErrorEnabled()) {
            log(LocationAwareLogger.ERROR_INT, format, new Object[]{arg}, null);
        }
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (isErrorEnabled()) {
            log(LocationAwareLogger.ERROR_INT, format, new Object[]{arg1, arg2}, null);
        }
    }

    @Override
    public void error(String format, Object... arguments) {
        if (isErrorEnabled()) {
            log(LocationAwareLogger.ERROR_INT, format, arguments, null);
        }
    }

    @Override
    public void error(String msg, Throwable t) {
        if (isErrorEnabled()) {
            log(LocationAwareLogger.ERROR_INT, msg, null, t);
        }
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return slfLogger.isErrorEnabled(marker);
    }

    @Override
    public void error(Marker marker, String msg) {
        if (isErrorEnabled(marker)) {
            log(marker, LocationAwareLogger.ERROR_INT, msg, null, null);
        }
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        if (isErrorEnabled(marker)) {
            log(marker, LocationAwareLogger.ERROR_INT, format, new Object[]{arg}, null);
        }
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        if (isErrorEnabled(marker)) {
            log(marker, LocationAwareLogger.ERROR_INT, format, new Object[]{arg1, arg2}, null);
        }
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        if (isErrorEnabled(marker)) {
            log(marker, LocationAwareLogger.ERROR_INT, format, arguments, null);
        }
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        if (isErrorEnabled(marker)) {
            log(marker, LocationAwareLogger.ERROR_INT, msg, null, t);
        }
    }
}
