package com.imooc.bilibili.service.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.imooc.bilibili.domain.exception.ConditionException;

import java.util.Calendar;
import java.util.Date;

/**
 * 用户token的加密生成与解码验证
 *
 * @author huangqiang
 * @date 2022/3/20 11:17
 * @see
 * @since
 */
public class TokenUtil {
    private static final String ISSUER = "签发者";

    // 加密生成access-token，使用userId标识用户
    public static String generateToken(Long userId) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        Calendar calendar = Calendar.getInstance();
        // 当前时间
        calendar.setTime(new Date());
        // token过期时间1小时
        calendar.add(Calendar.HOUR, 1);
        return JWT.create().withKeyId(String.valueOf(userId))// 唯一身份标识
                .withIssuer(ISSUER)// token签发者
                .withExpiresAt(calendar.getTime())// token过期时间
                .sign(algorithm);
    }

    // 加密生成refresh-token，使用userId标识用户
    public static String generateRefreshToken(Long userId) throws Exception {
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
        // 当前时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        // token过期时间7天
        calendar.add(Calendar.DAY_OF_MONTH, 7);
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }

    // 养成token
    public static Long verifyToken(String token) {
        try {
            // 解密出uid
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(), RSAUtil.getPrivateKey());
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            String userId = jwt.getKeyId();
            return Long.valueOf(userId);
        } catch (TokenExpiredException e) {
            // token过期
            throw new ConditionException("555", "token过期！");
        } catch (Exception e) {
            // 解密失败
            throw new ConditionException("非法用户token！");
        }
    }


}