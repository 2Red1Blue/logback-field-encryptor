package com.laiu.log.encrypt;

import java.util.Base64;

/**
 * @author liuzhixin
 * @Description:
 */
public class Base64Encrypt implements EncryptStrategy {
    @Override
    public String encrypt(String value) {
        return Base64.getEncoder().encodeToString(value.getBytes());
    }

    @Override
    public String decrypt(String value) {
        return new String(Base64.getDecoder().decode(value));
    }
}
