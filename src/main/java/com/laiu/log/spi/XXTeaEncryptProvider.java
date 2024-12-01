package com.laiu.log.spi;

import com.laiu.log.encrypt.EncryptStrategy;
import com.laiu.log.encrypt.XXTeaEncrypt;

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
