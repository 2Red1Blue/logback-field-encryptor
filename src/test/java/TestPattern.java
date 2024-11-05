import com.example.log.util.XXTEAUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liuzhixin
 * @Description:
 */
public class TestPattern {
    public static void main(String[] args) {
        //String text = "request:TestRequest(name= lll , password=,null, mobile=)13357394562, email=lll@ll.com , idCard=123123";
        String text = "BusinessHandler pushBusinessReportToRocketMq param:{\"acceptId\":\"11009336323967905792\",\"businessPlatform\":\"tianji\",\"carrier\":\"MOBILE\",\"channelCode\":\"quantum\",\"content\":\"【元保】金小海，您的保单已生效，请查收 https://dev.n6p.cn/pv2dHB\",\"doneTime\":\"20241024154908\",\"finalSend\":\"1\",\"mobile\":\"13979923451\",\"msgId\":\"11009336323967905792\",\"sendTime\":\"20241024154903\",\"smsType\":\"1\",\"source\":\"5\",\"status\":\"DELIVRD\",\"statusDesc\":\"\"}";

        String[] fieldNames = {"mobile"};

        for (String fieldName : fieldNames) {
            //String regex = String.format("(%s[:=])([^,\\s}]+?)(?=[,\\s}])", fieldName);
            //String regex = String.format("(%s[:=])\\s*(.*?)(?=[,\\s}\\)]|$)", fieldName);
            String regex = String.format("(\"%s\":\")(.*?)(\"}?[,}])", fieldName);
            //String regex = String.format("(%s[:=])\\s*([^,}\\s\\)]+?)(?=[,}\\s\\)])", fieldName);//无法匹配空格开头
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(text);

            if (matcher.find()) {
                String value = matcher.group(2);
                System.out.printf("Field: %s, Value: %s%n", fieldName, value);
            } else {
                System.out.printf("Field: %s, Not Found%n", fieldName);
            }
        }
        String decrypted = XXTEAUtil.decryptBase64StringToString("jaCN0eYnS/gHDQrsyznQxg==", "a$fHDF&G;lNFj%ea");
        System.out.println(decrypted);
        System.out.println(XXTEAUtil.encryptToBase64String("13979923451", "a$fHDF&G;lNFj%ea"));
        System.out.println(XXTEAUtil.encryptToBase64String("13979923451", "your-key-1"));

         }
}
