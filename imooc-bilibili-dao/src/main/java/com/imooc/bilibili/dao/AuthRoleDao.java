package com.imooc.bilibili.dao;

import com.imooc.bilibili.domain.auth.AuthRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色(等级)dao
 *
 * @author huangqiang
 * @date 2022/4/10 16:47
 * @see
 * @since
 */
@Mapper
public interface AuthRoleDao {

    //根据角色编码获取角色
    AuthRole getRoleByCode(String code);
}
