package com.laiu.log.spi;

import com.laiu.log.encrypt.EncryptStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * @author liuzhixin
 * @Description:
 */
public class EncryptStrategyFactory {
    private static final Logger logger = LoggerFactory.getLogger(EncryptStrategyFactory.class);
    private static final Map<String, EncryptStrategyProvider> providers = new HashMap<>();

    static {
        //加载所有实现
        ServiceLoader<EncryptStrategyProvider> loader = ServiceLoader.load(EncryptStrategyProvider.class);
        for(EncryptStrategyProvider provider : loader){
            providers.put(provider.getType(), provider);
            logger.info("load EncryptStrategyProvider:{}", provider.getType());
        }
    }

    public static EncryptStrategy createStrategy(String type, String key){
        EncryptStrategyProvider provider = providers.get(type);
        if(provider == null){
            throw new IllegalArgumentException("not found EncryptStrategyProvider: " + type);
        }
        return provider.createStrategy(key);
    }

    public static  boolean isSupport(String type){
        return providers.containsKey(type.toUpperCase());
    }
}
