package com.imooc.bilibili.api.support;

import org.springframework.stereotype.Component;
import com.imooc.bilibili.domain.exception.ConditionException;
import com.imooc.bilibili.service.util.TokenUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
/**
 * TODO
 *
 * @author huangqiang
 * @date 2022/3/20 21:52
 * @see
 * @since
 */
@Component
public class UserSupport {

    // 根据请求头中token解码出userId
    public Long getCurrentUserId(){
        // 抓取请求上下文
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        String token = requestAttributes.getRequest().getHeader("token");// 获取请求头中的token
        // 解密获得userId
        Long userId = TokenUtil.verifyToken(token);
        if(userId < 0){
            throw new ConditionException("非法用户！");
        }
        return userId;
    }
}