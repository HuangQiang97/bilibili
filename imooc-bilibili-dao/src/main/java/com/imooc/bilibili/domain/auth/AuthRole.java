package com.imooc.bilibili.domain.auth;

import java.util.Date;

/**
 * 角色表存储可选的用户等级（lv0，lv1，lv2）
 * <p>
 * `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
 * `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '角色名称',
 * `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '角色唯一编码',
 * `createTime` datetime DEFAULT NULL COMMENT '创建时间',
 * `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
 *
 * @author huangqiang
 * @date 2022/4/10 21:02
 * @see
 * @since
 */
public class AuthRole {

    private Long id;

    private String name;

    // lv0~lv2
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
