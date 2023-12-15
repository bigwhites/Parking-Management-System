package com.wtu.parking.interceptor;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureGenerationException;
import com.auth0.jwt.exceptions.TokenExpiredException;

import com.wtu.parking.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * @ClassName JwtInterceptor
 * @Author ChangLu
 * @Date 2021/8/15 18:05
 * @Description TODO
 */
@Component
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果是OPTIONS则放行
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod())) {
            return true;
        }

        // 获取token值
        String token = request.getHeader("Authorization");

        try {
            //验证token是否有误，若是验证失败会相应抛出指定的异常
            jwtUtil.verifyToken(token);
            return true;
        }catch(Exception e){
            response.setStatus(401);
            log.info("被拦截  "+e.getMessage());
            return false;
        }
    }
}
