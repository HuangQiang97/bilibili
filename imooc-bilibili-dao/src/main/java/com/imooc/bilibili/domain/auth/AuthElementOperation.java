package com.imooc.bilibili.domain.auth;

import java.util.Date;


/**
 * 页面元素操作表存储可对前端页面上某个元素进行的操作(比如可点击的上传视频按钮)
 * <p>
 * `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
 * `elementName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '页面元素名称',
 * `elementCode` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '页面元素唯一编码',
 * `operationType` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL COMMENT '操作类型：0可点击  1可见',
 * `createTime` datetime DEFAULT NULL COMMENT '创建时间',
 * `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
 *
 * @author huangqiang
 * @date 2022/4/10 21:02
 * @see
 * @since
 */

public class AuthElementOperation {

    private Long id;

    private String elementName;

    private String elementCode;

    // 操作类型：0可点击  1可见
    private String operationType;

    private Date createTime;

    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public String getElementCode() {
        return elementCode;
    }

    public void setElementCode(String elementCode) {
        this.elementCode = elementCode;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
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
