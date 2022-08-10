package com.imooc.bilibili.service;


import com.imooc.bilibili.dao.UserFollowingDao;
import com.imooc.bilibili.domain.FollowingGroup;
import com.imooc.bilibili.domain.User;
import com.imooc.bilibili.domain.UserFollowing;
import com.imooc.bilibili.domain.UserInfo;
import com.imooc.bilibili.domain.constant.UserConstant;
import com.imooc.bilibili.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 关注记录Service
 *
 * @author huangqiang
 * @date 2022/4/10 16:52
 * @see
 * @since
 */

@Service
public class UserFollowingService {

    @Autowired
    private UserFollowingDao userFollowingDao;

    // Service层中只应该引用其所对应的DAO组件，和同一层的Service组件
    @Autowired
    private FollowingGroupService followingGroupService;

    // Service层中只应该引用其所对应的DAO组件，和同一层的Service组件
    @Autowired
    private UserService userService;

    /**
     * 添加关注信息
     *
     * @param [userFollowing]
     * @return void
     * @throws
     */
    // 涉及到多个对数据库更改的操作，需要事务保证原子性和一致性
    @Transactional
    public void addUserFollowings(UserFollowing userFollowing) {
        Long groupId = userFollowing.getGroupId();
        if (groupId == null) {
            // 未设定关注分组时使用默认分组，type=2
            FollowingGroup followingGroup = followingGroupService.getByType(UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            userFollowing.setGroupId(followingGroup.getId());
        } else {
            // 设置的分组不存在
            FollowingGroup followingGroup = followingGroupService.getById(groupId);
            if (followingGroup == null) {
                throw new ConditionException("关注分组不存在！");
            }
        }
        // 被关注者ID
        Long followingId = userFollowing.getFollowingId();
        User user = userService.getUserById(followingId);
        if (user == null) {
            throw new ConditionException("被关注的用户不存在！");
        }
        // 删除原始关注者和被关注者的记录
        userFollowingDao.deleteUserFollowing(userFollowing.getUserId(), followingId);
        userFollowing.setCreateTime(new Date());
        // 保存新的关注者和被关注者的记录
        userFollowingDao.addUserFollowing(userFollowing);
    }

    /**
     * 得到用户所有的关注分组：默认分组被关注者信息+各个种类下被关注者信息
     * 第一步：获取关注的用户列表
     * 第二步：根据关注用户的id查询关注用户的基本信息
     * 第三步：将关注用户按关注分组进行分类
     *
     * @param [userId]
     * @return java.util.List<com.imooc.bilibili.domain.FollowingGroup>
     * @throws
     */
    public List<FollowingGroup> getUserFollowings(Long userId) {

        // 获得当前用户的全部关注记录
        List<UserFollowing> list = userFollowingDao.getUserFollowings(userId);
        // 被关注者ID集合
        Set<Long> followingIdSet = list.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        // 被关注者信息集合
        List<UserInfo> userInfoList = new ArrayList<>();
        if (followingIdSet.size() > 0) {
            userInfoList = userService.getUserInfoByUserIds(followingIdSet);
        }
        // 填充被关注者集合中，被关注者者自身的信息
        for (UserFollowing userFollowing : list) {
            for (UserInfo userInfo : userInfoList) {
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    // 为每条关注记录注入被关注者信息
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }
        // 获得用户的全部分组+默认+自定义
        List<FollowingGroup> groupList = followingGroupService.getByUserId(userId);
        // 该用户下全部分组的被关注者信息
        FollowingGroup allGroup = new FollowingGroup();
        allGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        allGroup.setFollowingUserInfoList(userInfoList);
        // 用户所有的关注分组：全部种类（自定义）被关注者信息+各个种类下被关注者信息
        List<FollowingGroup> result = new ArrayList<>();
        result.add(allGroup);
        // 遍历所有分组，
        for (FollowingGroup group : groupList) {
            List<UserInfo> infoList = new ArrayList<>();
            // 遍历所有关注记录
            for (UserFollowing userFollowing : list) {
                // 如果当前关注记录的种类和group种类匹配，将当前记录加入group种类下的集合中
                if (group.getId().equals(userFollowing.getGroupId())) {
                    infoList.add(userFollowing.getUserInfo());
                }

            }
            // 将当前group种类对应的被关注者信息注入集合
            group.setFollowingUserInfoList(infoList);
            result.add(group);
        }
        return result;
    }


    /**
     * 获得关注当前用户的用户信息
     * 第一步：获取当前用户的粉丝列表
     * 第二步：根据粉丝的用户id查询基本信息
     * 第三步：查询当前用户是否已经关注该粉丝
     *
     * @param [userId]
     * @return java.util.List<com.imooc.bilibili.domain.UserFollowing>
     * @throws
     */
    public List<UserFollowing> getUserFans(Long userId) {
        // 获取关注当前用户的关注记录
        List<UserFollowing> fanList = userFollowingDao.getUserFans(userId);
        // 关注当前用户的用户ID
        Set<Long> fanIdSet = fanList.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        if (fanIdSet.size() > 0) {
            // 关注当前用户的用户信息
            userInfoList = userService.getUserInfoByUserIds(fanIdSet);
        }
        // 获取当前用户的关注记录
        List<UserFollowing> followingList = userFollowingDao.getUserFollowings(userId);
        for (UserFollowing fan : fanList) {
            for (UserInfo userInfo : userInfoList) {
                // 关注记录下关注者与用户信息相匹配
                if (fan.getUserId().equals(userInfo.getUserId())) {
                    userInfo.setFollowed(false);
                    fan.setUserInfo(userInfo);
                }
            }
            // 从粉丝中找的互关的粉丝
            for (UserFollowing following : followingList) {
                if (following.getFollowingId().equals(fan.getUserId())) {
                    fan.getUserInfo().setFollowed(true);
                }
            }
        }
        return fanList;
    }

    /**
     * 添加分组
     *
     * @param [followingGroup]
     * @return java.lang.Long
     * @throws
     */
    public Long addUserFollowingGroups(FollowingGroup followingGroup) {

        followingGroup.setCreateTime(new Date());
        followingGroup.setType(UserConstant.USER_FOLLOWING_GROUP_TYPE_USER);
        followingGroupService.addFollowingGroup(followingGroup);
        return followingGroup.getId();
    }

    /**
     * 获得用户拥有的全部分组
     *
     * @param [userId]
     * @return java.util.List<com.imooc.bilibili.domain.FollowingGroup>
     * @throws
     */
    public List<FollowingGroup> getUserFollowingGroups(Long userId) {

        return followingGroupService.getUserFollowingGroups(userId);
    }

    /**
     * 检查当前用户是否关注查询到的用户
     *
     * @param [userInfoList:查询用户列表, userId]
     * @return java.util.List<com.imooc.bilibili.domain.UserInfo>
     * @throws
     */
    public List<UserInfo> checkFollowingStatus(List<UserInfo> userInfoList, Long userId) {

        // 关注的用户
        List<UserFollowing> userFollowingList = userFollowingDao.getUserFollowings(userId);
        for (UserInfo userInfo : userInfoList) {
            userInfo.setFollowed(false);
            for (UserFollowing userFollowing : userFollowingList) {
                // 粉丝ID能在当前用户的关注列表中找到
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    userInfo.setFollowed(true);
                }
            }
        }
        return userInfoList;
    }
}
