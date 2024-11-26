package com.laiu.log.json;

import ch.qos.logback.classic.spi.ILoggingEvent;
import com.laiu.log.FieldEncryptService;
import com.fasterxml.jackson.core.JsonGenerator;
import net.logstash.logback.composite.AbstractFieldJsonProvider;
import net.logstash.logback.composite.JsonWritingUtils;

import java.io.IOException;

/**
 * @author liuzhixin
 * @Description:
 */
public class EncryptMessageJsonProvider extends AbstractFieldJsonProvider<ILoggingEvent> {
    private FieldEncryptService fieldEncryptService = FieldEncryptService.getInstance();

    @Override
    public void writeTo(JsonGenerator generator, ILoggingEvent event) throws IOException {
        String originalMessage = event.getFormattedMessage();
        String encryptedMessage = fieldEncryptService.encryptMessage(originalMessage);
        JsonWritingUtils.writeStringField(generator, getFieldName(), encryptedMessage);
    }
}
