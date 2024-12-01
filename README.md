# Logback Field Encryptor
一个用于日志字段加密的 Logback 转换器，支持多字段、多种加密方式和自定义匹配模式。
## 快速开始

### 1. 添加依赖
```xml
<dependency>
    <groupId>com.yuanbao</groupId>
    <artifactId>logback-field-encryptor</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```
### 2. 配置 Logback

在 logback-spring.xml 或 logback.xml 中添加：

```xml
<!-- 1. 定义转换器 -->
<conversionRule conversionWord="fieldEncrypt"
                converterClass="com.laiu.log.convertor.FieldEncryptConverter"/>

        <!-- 2. 在 pattern 中使用转换器 -->
<appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
<encoder>
    <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %fieldEncrypt{%msg}%n</pattern>
</encoder>
</appender>
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
...
<pattern>...%encryptMobile{%msg}...</pattern>
...
</appender>
        ...
```
### 3. 创建配置文件

在项目 resources 目录下创建 field-encrypt.properties：
```properties
# 需要加密的字段列表
encrypt.fields=mobile,idCard,email

# mobile 字段配置
mobile.encrypt.type=XXTEA # 默认的加密方式
mobile.encrypt.key=your-key-1
# 自定义匹配模式
mobile.patterns=(%s:)([^,}\\s]+),(\"%s\":\")(.*?)(\"}?[,}])

# idCard 字段配置
idCard.encrypt.type=XXTEA
idCard.encrypt.key=your-key-2

# email 字段配置
email.encrypt.type=BASE64
```
>以上内容皆为可选，目前仅支持XXTEA加密和BASE64编码
默认的加密字段: mobile
默认的匹配规则：~~1.普通文本:`(%s:)([^,}\\s]+)`, 2. JSON文本:`(\"%s\":\")(.*?)(\"}?[,}])`,  3. 对象toString: `(%s=)([^,}\\s]+)`~~
> 1. 普通对象文本`(%s[:=])\\s*([^,}\\s\\)]+?)(?=[,}\\s\\)])`
> 2. JSON:`("%s":")(.*?)("}?[,}])`


### 自定义加密方式

你可以通过以下步骤添加自定义的加密方式：

1. 实现`EncryptStrategy`接口，创建你的加密策略类
2. 实现`EncryptStrategyProvider`接口，创建对应的Provider类
3. 在`META-INF/services/com.yuanbao.sms.encrypt.spi.EncryptStrategyProvider`文件中添加你的Provider类的全限定名
4. 在配置文件中使用你的自定义加密类型

示例：
```java
// 1. 实现加密策略
public class MyEncrypt implements EncryptStrategy{

    @Override
    public String encrypt(String value) {
        //实现加密逻辑
        return "";
    }

    @Override
    public String decrypt(String value) {
        //实现解密逻辑
        return "";
    }
}
// 2. 实现Provider
public class MyEncryptProvider implements EncryptStrategyProvider{
    @Override
    public String getType() {
        return "MYTYPE";
    }

    @Override
    public EncryptStrategy createStrategy(String key) {
        return new MyEncrypt();
    }
}
```