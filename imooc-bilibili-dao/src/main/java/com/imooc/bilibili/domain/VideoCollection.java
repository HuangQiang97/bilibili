package com.imooc.bilibili.domain;


import java.util.Date;

/**
 * 视频收藏
 *
 * @author huangqiang
 * @date 2022/4/15 17:47
 * @see
 * @since
 */
public class VideoCollection {

    private Long id;

    // 视频ID
    private Long videoId;

    // 用户ID
    private Long userId;

    // 收藏分组
    private Long groupId;

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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
