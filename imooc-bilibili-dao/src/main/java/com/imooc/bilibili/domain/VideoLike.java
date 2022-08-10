package com.imooc.bilibili.domain;


import java.util.Date;

/**
 * 视频点赞记录
 *
 * @author huangqiang
 * @date 2022/4/15 17:33
 * @see
 * @since
 */
public class VideoLike {

    private Long id;

    // 点赞用户
    private Long userId;

    // 被点赞视频
    private Long videoId;

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

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
