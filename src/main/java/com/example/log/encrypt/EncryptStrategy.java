package com.example.log.encrypt;

/**
 * @author liuzhixin
 * @Description:
 */
public interface EncryptStrategy {
    String encrypt(String value);
    String decrypt(String value);
}
