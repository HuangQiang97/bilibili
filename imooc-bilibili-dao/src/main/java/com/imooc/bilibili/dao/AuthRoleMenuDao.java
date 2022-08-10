package com.imooc.bilibili.dao;

import com.imooc.bilibili.domain.auth.AuthRoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

/**
 * 页面权限dao
 *
 * @author huangqiang
 * @date 2022/4/10 16:47
 * @see
 * @since
 */
@Mapper
public interface AuthRoleMenuDao {
    // 根据角色权限查询可访问页面
    List<AuthRoleMenu> getAuthRoleMenusByRoleIds(Set<Long> roleIdSet);
}
