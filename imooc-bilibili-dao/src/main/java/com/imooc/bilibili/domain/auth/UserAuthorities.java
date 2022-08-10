package com.imooc.bilibili.domain.auth;

import java.util.List;

/**
 * 用户权限封装bean，包括页面按钮点击权限、特定页面访问权限
 *
 * @author huangqiang
 * @date 2022/4/10 21:02
 * @see
 * @since
 */
public class UserAuthorities {

    // 页面按钮点击权限
    List<AuthRoleElementOperation> roleElementOperationList;

    // 特定页面访问权限
    List<AuthRoleMenu> roleMenuList;

    public List<AuthRoleElementOperation> getRoleElementOperationList() {
        return roleElementOperationList;
    }

    public void setRoleElementOperationList(List<AuthRoleElementOperation> roleElementOperationList) {
        this.roleElementOperationList = roleElementOperationList;
    }

    public List<AuthRoleMenu> getRoleMenuList() {
        return roleMenuList;
    }

    public void setRoleMenuList(List<AuthRoleMenu> roleMenuList) {
        this.roleMenuList = roleMenuList;
    }
}
