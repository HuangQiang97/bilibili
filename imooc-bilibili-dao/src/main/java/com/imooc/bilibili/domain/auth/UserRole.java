package com.imooc.bilibili.domain.auth;

import java.util.Date;

/**
 * 用户-角色关联表存储用户与角色的关联关系（指明用户所处等级）
 * <p>
 * `id` bigint NOT NULL AUTO_INCREMENT,
 * `userId` bigint DEFAULT NULL COMMENT '用户id',
 * `roleId` bigint DEFAULT NULL COMMENT '角色id',
 * `createTime` datetime DEFAULT NULL COMMENT '创建时间',
 *
 * @author huangqiang
 * @date 2022/4/10 21:02
 * @see
 * @since
 */
public class UserRole {

    private Long id;

    private Long userId;

    private Long roleId;

    // 所处等级名称，由级联查询获得
    private String roleName;

    // 所处等级代码，由级联查询获得
    private String roleCode;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }
}
