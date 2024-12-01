package com.laiu.log.convertor;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import com.laiu.log.FieldEncryptService;


/**
 * @author liuzhixin
 * @Description:
 */
public class FieldEncryptConverter extends ClassicConverter {
    private FieldEncryptService fieldEncryptService = FieldEncryptService.getInstance();

    @Override
    public String convert(ILoggingEvent event) {
        return fieldEncryptService.encryptMessage(event.getFormattedMessage());
    }
}