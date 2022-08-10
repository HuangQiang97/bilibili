package com.imooc.bilibili.domain;


/**
 * DFS 中存储的文件bean
 *
 * @author huangqiang
 * @date 2022/4/14 22:51
 * @see
 * @since
 */

import java.util.Date;

public class DFSFile {

    private Long id;
    
    // 路径
    private String url;

    private String type;

    // 文件摘要，用于秒传
    private String md5;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
