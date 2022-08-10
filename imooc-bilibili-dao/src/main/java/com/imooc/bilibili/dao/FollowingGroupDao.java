package com.imooc.bilibili.dao;


import com.imooc.bilibili.domain.FollowingGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 关注分组DAO
 * `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
 * `userId` bigint DEFAULT NULL COMMENT '用户id',
 * `name` varchar(50) DEFAULT NULL COMMENT '关注分组名称',
 * `type` varchar(5) DEFAULT NULL COMMENT '关注分组类型：0特别关注  1悄悄关注 2默认分组  3用户自定义分组',
 * `createTime` datetime DEFAULT NULL COMMENT '创建时间',
 * `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
 *
 * @author huangqiang
 * @date 2022/4/10 13:58
 * @see
 * @since
 */
@Mapper
public interface FollowingGroupDao {


    FollowingGroup getByType(String type);

    FollowingGroup getById(Long id);

    // 获得某个用户的所有分组：默认分组+自定义分组
    List<FollowingGroup> getByUserId(Long userId);

    Integer addFollowingGroup(FollowingGroup followingGroup);

    // 获得某个用户的自定义分组
    List<FollowingGroup> getUserFollowingGroups(Long userId);
}
