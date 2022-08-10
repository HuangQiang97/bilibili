package com.imooc.bilibili.dao;


import com.imooc.bilibili.domain.UserFollowing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 关注记录DAO
 * `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
 * `userId` bigint DEFAULT NULL COMMENT '用户id',
 * `followingId` int DEFAULT NULL COMMENT '关注用户id',
 * `groupId` int DEFAULT NULL COMMENT '关注分组id',
 * `createTime` datetime DEFAULT NULL COMMENT '创建时间',
 *
 * @author huangqiang
 * @date 2022/4/10 13:58
 * @see
 * @since
 */
@Mapper
public interface UserFollowingDao {

    // 根据关注者与被关注者删除关注记录
    Integer deleteUserFollowing(@Param("userId") Long userId, @Param("followingId") Long followingId);

    Integer addUserFollowing(UserFollowing userFollowing);

    // 获得用户关注的所有用户
    List<UserFollowing> getUserFollowings(Long userId);

    // 获得关注本用户的所有用户
    List<UserFollowing> getUserFans(Long userId);
}
