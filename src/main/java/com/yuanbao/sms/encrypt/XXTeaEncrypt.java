package com.yuanbao.sms.encrypt;

import com.yuanbao.sms.util.XXTEAUtil;

/**
 * @author liuzhixin
 * @Description:
 */
public class XXTeaEncrypt implements com.yuanbao.sms.encrypt.EncryptStrategy {
    private final String key;

    public XXTeaEncrypt(String key) {
        this.key = key;
    }

    @Override
    public String encrypt(String value) {
        return XXTEAUtil.encryptToBase64String(value, key);
    }

    @Override
    public String decrypt(String value) {
        return new String(XXTEAUtil.decryptBase64String(value, key));
    }
}
