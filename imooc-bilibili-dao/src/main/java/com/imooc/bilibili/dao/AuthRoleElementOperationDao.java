package com.imooc.bilibili.dao;

import com.imooc.bilibili.domain.auth.AuthRoleElementOperation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 页面元素点击dao
 *
 * @author huangqiang
 * @date 2022/4/10 16:47
 * @see
 * @since
 */
@Mapper
public interface AuthRoleElementOperationDao {
    // 根据角色权限查询可点击页面元素
    List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(@Param("roleIdSet") Set<Long> roleIdSet);
}
