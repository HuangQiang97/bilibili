package com.imooc.bilibili.service.handler;

import com.imooc.bilibili.domain.exception.ConditionException;
import com.imooc.bilibili.domain.JsonResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 *
 * @author huangqiang
 * @date 2022/3/19 23:00
 * @see
 * @since
 */
// controller通知增强，针对controller设定触发事件与处理方法
// @ExceptionHandler:进行全局异常处理
// @InitBinder:绑定前台请求参数到Model中，全局数据预处理
// @ModelAttribute:绑定键值对到Model中,全局数据绑定
// https://juejin.cn/post/6844904168025489421
@ControllerAdvice
// 全局异常处理，所以拥有高优先级
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CommonGlobalExceptionHandler {
    // 拦截异常的种类，拦截全局异常时使用Exception.class
    @ExceptionHandler(value = Exception.class)
    // 拦截异常后可以选择返回的json对象
    @ResponseBody
    public JsonResponse<String> commonExceptionHandler(HttpServletRequest request,Exception exception){
        String msg=exception.getMessage();
        // 主动抛出异常的处理，如密码错误抛出异常
        if(exception instanceof ConditionException){
            String code=((ConditionException)exception).getCode();
            return new JsonResponse<>(code,msg);
        }
        // 其它未知异常
        return new JsonResponse<>("500",msg);
    }
}