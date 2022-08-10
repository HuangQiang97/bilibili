package com.imooc.bilibili.domain.auth;

import java.util.Date;

/**
 * 角色-页面关联表存储角色与可访问页面的关系(指明角色与页面间的可访问关系，
 * 比如lv0不能访问邀请码购买页面，lv1可以正常访问)
 * <p>
 * `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
 * `roleId` bigint DEFAULT NULL COMMENT '角色id',
 * `menuId` bigint DEFAULT NULL COMMENT '页面菜单id',
 * `createTime` datetime DEFAULT NULL COMMENT '创建时间',
 *
 * @author huangqiang
 * @date 2022/4/10 21:02
 * @see
 * @since
 */
public class AuthRoleMenu {

    private Long id;

    private Long roleId;

    private Long menuId;

    private Date createTime;

    // 联表查询，加快处理速度
    // 增大了存储需求，如果被包含对象体积较大，在大量查询时会带来较大压力
    private AuthMenu authMenu;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getMenuId() {
        return menuId;
    }

    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public AuthMenu getAuthMenu() {
        return authMenu;
    }

    public void setAuthMenu(AuthMenu authMenu) {
        this.authMenu = authMenu;
    }
}
