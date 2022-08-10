package com.imooc.bilibili.domain.exception;

/**
 * 条件异常，携带错误码，方便前后端交互
 *
 * @author huangqiang
 * @date 2022/3/19 22:56
 * @see
 * @since
 */
public class ConditionException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private String code;

    public ConditionException(String code, String name){
        super(name);
        this.code = code;
    }

    public ConditionException(String name){
        super(name);
        code = "500";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}