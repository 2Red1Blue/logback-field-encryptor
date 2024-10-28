package com.yuanbao.sms.spi;

import com.yuanbao.sms.encrypt.EncryptStrategy;
import com.yuanbao.sms.encrypt.XXTeaEncrypt;

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
