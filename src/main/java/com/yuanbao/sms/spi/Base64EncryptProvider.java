package com.yuanbao.sms.spi;

import com.yuanbao.sms.encrypt.Base64Encrypt;
import com.yuanbao.sms.encrypt.EncryptStrategy;

/**
 * @author liuzhixin
 * @Description:
 */
public class Base64EncryptProvider implements EncryptStrategyProvider{
    @Override
    public String getType() {
        return "BASE64";
    }

    @Override
    public EncryptStrategy createStrategy(String key) {
        return new Base64Encrypt();
    }
}
