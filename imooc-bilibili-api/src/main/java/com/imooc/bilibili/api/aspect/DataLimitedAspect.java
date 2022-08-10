package com.imooc.bilibili.api.aspect;

import com.imooc.bilibili.api.support.UserSupport;
import com.imooc.bilibili.domain.UserMoment;
import com.imooc.bilibili.domain.auth.UserRole;
import com.imooc.bilibili.domain.constant.AuthRoleConstant;
import com.imooc.bilibili.domain.exception.ConditionException;
import com.imooc.bilibili.service.UserRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Order(1)
@Component
@Aspect
public class DataLimitedAspect {

    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;

    // 拦截带有DataLimitedAspect的方法
    @Pointcut("@annotation(com.imooc.bilibili.domain.annotation.DataLimited)")
    public void check() {
    }

    // 在带有DataLimitedAspect注解的方法执行前进行前置处理
    @Before("check()")
    public void doBefore(JoinPoint joinPoint) {
        Long userId = userSupport.getCurrentUserId();
        // 用户等级ID
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        // 用户等级ID对应的编码
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        Object[] args = joinPoint.getArgs();
        // 遍历被注解方法参数，只对发布动态的方法起作用
        for (Object arg : args) {
            if (arg instanceof UserMoment) {
                UserMoment userMoment = (UserMoment) arg;
                String type = userMoment.getType();
                // 发布直播动态且等级非lv2将报权限异常
                if ("2".equals(type) && !roleCodeSet.contains(AuthRoleConstant.ROLE_LV2)) {
                    throw new ConditionException("lv2用户才能发布直播动态");
                }
            }
        }
    }
}
