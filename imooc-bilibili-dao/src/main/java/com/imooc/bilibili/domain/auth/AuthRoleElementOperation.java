package com.imooc.bilibili.domain.auth;

import java.util.Date;


/**
 * 角色-元素操作关联表存储角色与页面元素操作间关联（指明不同等级与不同可操作元素间关系，
 * 比如lv0不能点击视频上传按钮，lv1可以点击）
 * <p>
 * `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
 * `roleId` bigint DEFAULT NULL COMMENT '角色id',
 * `elementOperationId` bigint DEFAULT NULL COMMENT '元素操作id',
 * `createTime` datetime DEFAULT NULL COMMENT '创建时间',
 *
 * @author huangqiang
 * @date 2022/4/10 21:02
 * @see
 * @since
 */
public class AuthRoleElementOperation {

    private Long id;

    private Long roleId;

    private Long elementOperationId;

    private Date createTime;

    // 联表查询，直接封装AuthElementOperation，
    // 免得需要AuthElementOperation时再去使用elementOperationId去数据库查询
    // 增大了存储需求，如果被包含对象体积较大，在大量查询时会带来较大压力
    private AuthElementOperation authElementOperation;

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

    public Long getElementOperationId() {
        return elementOperationId;
    }

    public void setElementOperationId(Long elementOperationId) {
        this.elementOperationId = elementOperationId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public AuthElementOperation getAuthElementOperation() {
        return authElementOperation;
    }

    public void setAuthElementOperation(AuthElementOperation authElementOperation) {
        this.authElementOperation = authElementOperation;
    }
}
