package com.imooc.bilibili.dao;

import com.alibaba.fastjson.JSONObject;
import com.imooc.bilibili.domain.RefreshTokenDetail;
import com.imooc.bilibili.domain.User;
import com.imooc.bilibili.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户相关DAO
 * <p>
 * t_user
 * `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
 * `phone` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '手机号',
 * `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '邮箱',
 * `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '密码',
 * `salt` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '盐值',
 * `createTime` datetime DEFAULT NULL COMMENT '创建时间',
 * `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
 * <p>
 * t_user_info
 * `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
 * `userId` bigint DEFAULT NULL COMMENT '用户id',
 * `nick` varchar(100) DEFAULT NULL COMMENT '昵称',
 * `avatar` varchar(255) DEFAULT NULL COMMENT '头像',
 * `sign` text COMMENT '签名',
 * `gender` varchar(2) DEFAULT NULL COMMENT '性别：0男 1女 2未知',
 * `birth` varchar(20) DEFAULT NULL COMMENT '生日',
 * `createTime` datetime DEFAULT NULL COMMENT '创建时间',
 * `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
 *
 * @author huangqiang
 * @date 2022/3/20 0:40
 * @see
 * @since
 */
@Mapper
public interface UserDao {

    // 根据手机号获取到用户
    User getUserByPhone(String phone);

    // 添加用户
    Integer addUser(User user);

    // 添加用户信息
    Integer addUserInfo(UserInfo userInfo);

    // 根据userId获得用户信息
    User getUserById(Long id);

    // 根据userId获得用户信息
    UserInfo getUserInfoByUserId(Long userId);

    // 更新用户
    void updateUsers(User user);

    // 更新用户信息
    Integer updateUserInfos(UserInfo userInfo);

    // 根据用户ID批量获得用户信息
    List<UserInfo> getUserInfoByUserIds(Set<Long> userIdList);

    // 获得查询匹配用户数
    Integer pageCountUserInfos(Map<String, Object> params);

    // 分页模糊查询
    List<UserInfo> pageListUserInfos(JSONObject params);

    // 删除用户的refreshToken
    Integer deleteRefreshToken(@Param("refreshToken") String refreshToken,
                               @Param("userId") Long userId);

    // 添加用户的refreshToken
    Integer addRefreshToken(@Param("refreshToken") String refreshToken,
                            @Param("userId") Long userId,
                            @Param("createTime") Date createTime);

    // 查询用户的refreshToken
    RefreshTokenDetail getRefreshTokenDetail(String refreshToken);

    List<UserInfo> batchGetUserInfoByUserIds(Set<Long> userIdList);

    Long getUserInfoIdByUserId(Long userLd);
}
