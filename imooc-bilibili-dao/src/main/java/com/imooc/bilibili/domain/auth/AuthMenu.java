package com.imooc.bilibili.domain.auth;

import java.util.Date;

/**
 * 页面访问表存储页面（比如购买邀请码的页面）
 * <p>
 * `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
 * `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '菜单项目名称',
 * `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '唯一编码',
 * `createTime` datetime DEFAULT NULL COMMENT '创建时间',
 * `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
 *
 * @author huangqiang
 * @date 2022/4/10 21:02
 * @see
 * @since
 */
public class AuthMenu {

    private Long id;

    private String name;

    private String code;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
