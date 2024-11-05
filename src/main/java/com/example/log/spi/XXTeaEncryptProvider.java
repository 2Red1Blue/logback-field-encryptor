package com.example.log.spi;

import com.example.log.encrypt.EncryptStrategy;
import com.example.log.encrypt.XXTeaEncrypt;

/**
 * @author liuzhixin
 * @Description:
 */
public class XXTeaEncryptProvider implements EncryptStrategyProvider{
    @Override
    public String getType() {
        return "XXTEA";
    }

    @Override
    public EncryptStrategy createStrategy(String key) {
        return new XXTeaEncrypt(key);
    }
}
