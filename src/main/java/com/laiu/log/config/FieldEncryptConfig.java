package com.laiu.log.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liuzhixin
 * @Description:
 */

public class FieldEncryptConfig {
    private Map<String, EncryptField> encryptFields = new HashMap<>();

    public Map<String, EncryptField> getEncryptFields() {
        return encryptFields;
    }

    public static final String DEFAULT_PATTERN = "(\\\\*\"*%s\\\\*\"*\\s*[:=]\\s*)(\\\\*\"*)(.*?)(?=,|\\}|\\)|$)";

    public static class EncryptField {
        private String fieldName;
        private String encryptType; // "XXTEA" or "BASE64"
        private String encryptKey;  // 仅XXTEA需要
        private List<String> patterns;
        //或"(%s[:=])\\s*(.*?)(?=[,\\s}\\)]|$)"
        public EncryptField() {
            // 默认的匹配模式,使用非贪婪匹配防止匹配过多
            patterns = Arrays.asList(
                    //"(%s:)([^,}\\s]+)",           // 普通文本模式，在逗号、}或空白字符处停止
                    //"(\"%s\":\")(.*?)(\"}?[,}])",  // JSON模式，在"}或,"处停止  //无法处理嵌套JSON
                    //"(\"%s\":\\s*\")(.*?)(?<!\\\\)\"(?=\\s*[,}])",
                    //"(\"*%s\"*\\s*:\\s*\"*)(.*?)(?<!\\\\)\"*(?=\\s*[},])",
                    //"(%s=)([^,}\\s]+)"            // toString模式
                    //"(%s[:=])\\s*([^,}\\s\\)]+?)(?=[,}\\s\\)])" //存在mobile:{}放在结尾匹配不上的情况
                    //"(%s[:=])\\s*(.*?)(?=,|\\}|\\)|$)"
                    //"(\\\\*\"*%s\\\\*\"*\\s*[:=]\\s*)(\\\\*\"*)(.*?)(?=,|\\}|\\)|$)"
                    DEFAULT_PATTERN
            );
        }
        public String getFieldName() {
            return fieldName;
        }
        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }
        public String getEncryptType() {
            return encryptType;
        }
        public void setEncryptType(String encryptType) {
            this.encryptType = encryptType;
        }
        public String getEncryptKey() {
            return encryptKey;
        }
        public void setEncryptKey(String encryptKey) {
            this.encryptKey = encryptKey;
        }
        public List<String> getPatterns() {
            return patterns;
        }
        public void setPatterns(List<String> patterns) {
            this.patterns = patterns;
        }
    }

    public void addField(String fieldName, String encryptType, String encryptKey) {
        addField(fieldName, encryptType, encryptKey, null);
    }

    public void addField(String fieldName, String encryptType, String encryptKey, List<String> patterns) {
        EncryptField field = new EncryptField();
        field.setFieldName(fieldName);
        field.setEncryptType(encryptType);
        field.setEncryptKey(encryptKey);
        if (patterns!=null &&!patterns.isEmpty()){
            field.setPatterns(patterns);
        }
        encryptFields.put(fieldName, field);
    }

}
