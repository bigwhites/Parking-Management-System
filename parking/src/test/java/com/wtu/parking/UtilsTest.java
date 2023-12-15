package com.wtu.parking;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UtilsTest {

    @Test
    void ttime(){
        String jstr = """
                {
                    "result": [{
                        "rect": [622, 825, 1231, 1008],
                        "landmarks": [
                            [622, 824],
                            [1238, 820],
                            [1238, 1009],
                            [620, 1014]
                        ],
                        "number": "沪KR9888",
                        "roi_height": 190,
                        "confidence": 0.84,
                        "class": "single"
                    }],
                    "count": 1,
                    "log_id": "41cfc2b0-bd9b-11ed-9724-0000000032d8"
                }
                """;
        JSONObject jsonObject = JSON.parseObject(jstr);
        String number = jsonObject.getJSONArray("result").getJSONObject(0).getString("number");
        System.out.println(number);
    }

    @Test
    void B64(){
        Base64 base64= new Base64();
        String text="ssss";
        byte[] bytes = text.getBytes();
        String string = base64.encodeToString(bytes);
        System.out.println(string);

    }
}
