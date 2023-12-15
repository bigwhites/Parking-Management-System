package com.wtu.parking.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

/**
 * JWT工具类
 *
 * @author changlu
 * @date 2021/08/09 16:27
 **/
@Component
@Data
@Slf4j
public class JwtUtil {

    // header头部声明类型
    //@Value("${token.header}")
    private String header="token";

    // signature中的秘钥
    //@Value("${token.secret}")
    private String secret="789";

    // 过期时间
   // @Value("${token.expireTime}")
    private  Integer expireTime;

    /**
     * 默认过期时间为1天
     */
    private static  Integer DEFAULT_EXPIRETIME = 1;

    /**
     * 生成JWT token
     * @param playLoadMap 封装包含用户信息的map
     * @return
     */
    public String createToken(Map<String, String> playLoadMap) {
        // playload主体信息为空则不生成token
        if (CollectionUtils.isEmpty(playLoadMap)) {
            return null;
        }

        // 过期时间：若是配置文件不配置就使用默认过期时间(1天)
        Calendar ca = Calendar.getInstance();
        if (expireTime == null || expireTime <= 0) {
            expireTime = DEFAULT_EXPIRETIME;
        }
        ca.add(Calendar.DATE, expireTime);

        //  创建JWT的token对象
        JWTCreator.Builder builder = JWT.create();
        playLoadMap.forEach(builder::withClaim);
        // 设置发布事件
        builder.withIssuedAt(new Date());
        // 过期时间
        builder.withExpiresAt(ca.getTime());
        // 签名加密
        String token = builder.sign(Algorithm.HMAC256(secret));
        return token;
    }

    /**
     * 从token中获取到指定指定keyName的value值
     * @param keyName 指定的keyname
     * @param token token字符串
     * @return 对应keyName的value值
     */
    public String getTokenClaimByName(String keyName,String token){
        DecodedJWT decode = JWT.decode(token);
        return decode.getClaim(keyName).asString();
    }

    /**
     * 验证JwtToken 不抛出异常说明验证通过
     * @param token JwtToken数据
     */
    public void verifyToken(String token)throws Exception{
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secret)).build();
        jwtVerifier.verify(token);
    }

}
