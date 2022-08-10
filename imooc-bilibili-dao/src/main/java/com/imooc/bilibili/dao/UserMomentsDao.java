package com.imooc.bilibili.dao;

import com.imooc.bilibili.domain.UserMoment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 动态记录DAO
 * `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
 * `userId` bigint DEFAULT NULL COMMENT '用户id',
 * `type` varchar(5) DEFAULT NULL COMMENT '动态类型：0视频 1直播 2专栏动态',
 * `contentId` bigint DEFAULT NULL COMMENT '内容详情id',
 * `createTime` datetime DEFAULT NULL COMMENT '创建时间',
 * `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
 *
 * @author huangqiang
 * @date 2022/4/11 20:52
 * @see
 * @since
 */

@Mapper
public interface UserMomentsDao {

    Integer addUserMoments(UserMoment userMoment);
}
