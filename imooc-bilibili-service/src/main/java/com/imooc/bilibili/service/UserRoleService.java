package com.imooc.bilibili.service;

import com.imooc.bilibili.dao.UserRoleDao;
import com.imooc.bilibili.domain.auth.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 用户角色service
 *
 * @author huangqiang
 * @date 2022/4/10 16:47
 * @see
 * @since
 */
@Service
public class UserRoleService {

    @Autowired
    private UserRoleDao userRoleDao;

    // 根据用户ID获取用户角色（等级）
    public List<UserRole> getUserRoleByUserId(Long userId) {
        return userRoleDao.getUserRoleByUserId(userId);
    }

    // 添加用户角色
    public void addUserRole(UserRole userRole) {
        userRole.setCreateTime(new Date());
        userRoleDao.addUserRole(userRole);
    }
}
