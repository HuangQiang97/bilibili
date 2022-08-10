package com.imooc.bilibili.domain;


import java.util.Date;

/**
 * 视频可选标签
 *
 * @author huangqiang
 * @date 2022/4/15 15:23
 * @see
 * @since
 */
public class VideoTag {

    private Long id;
    // 视频ID
    private Long videoId;

    // 标签ID
    private Long tagId;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
