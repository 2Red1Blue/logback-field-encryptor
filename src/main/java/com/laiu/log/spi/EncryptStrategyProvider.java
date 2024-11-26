package com.laiu.log.spi;

import com.laiu.log.encrypt.EncryptStrategy;

/**
 * @author liuzhixin
 * @Description:
 */
public interface EncryptStrategyProvider {
    /**
     * 获取加密策略类型名称
     */
    String getType();

    /**
     * 创建加密策略实例
     */
    EncryptStrategy createStrategy(String key);
}