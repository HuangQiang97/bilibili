package com.imooc.bilibili.domain;

import java.util.Date;

/**
 * 弹幕消息bean
 *
 * @author huangqiang
 * @date 2022/4/20 21:45
 * @see
 * @since
 */
public class Danmu {

    private Long id;

    // 发送者ID
    private Long userId;

    // 弹幕所属视频ID
    private Long videoId;

    // 弹幕内容
    private String content;

    // 弹幕在视频哪个时间发送
    private String danmuTime;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDanmuTime() {
        return danmuTime;
    }

    public void setDanmuTime(String danmuTime) {
        this.danmuTime = danmuTime;
    }


}
