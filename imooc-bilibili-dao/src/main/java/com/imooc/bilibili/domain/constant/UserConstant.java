package com.imooc.bilibili.domain.constant;

/**
 * 用户相关常量，用于用户属性缺失时的默认值
 *
 * @author huangqiang
 * @date 2022/3/20 10:54
 * @see
 * @since
 */
public interface UserConstant {
    public static final String GENDER_MALE = "0";

    public static final String GENDER_FEMALE = "1";

    public static final String GENDER_UNKNOW = "0";

    public static final String DEFAULT_BIRTH = "1999-10-01";

    public static final String DEFAULT_NICK = "萌新";

    public static final String USER_FOLLOWING_GROUP_TYPE_DEFAULT = "2";

    public static final String USER_FOLLOWING_GROUP_TYPE_USER = "3";

    public static final String USER_FOLLOWING_GROUP_ALL_NAME = "全部关注";
}