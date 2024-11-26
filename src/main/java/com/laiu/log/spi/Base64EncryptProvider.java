package com.laiu.log.spi;

import com.laiu.log.encrypt.Base64Encrypt;
import com.laiu.log.encrypt.EncryptStrategy;

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
