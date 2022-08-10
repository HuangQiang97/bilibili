package com.imooc.bilibili.domain;


import java.util.Date;
import java.util.List;

/**
 * 视频评论信息
 *
 * @author huangqiang
 * @date 2022/4/15 23:10
 * @see
 * @since
 */

public class VideoComment {

    private Long id;

    // 被评论视频信息
    private Long videoId;

    // 发表评论用户
    private Long userId;

    private String comment;

    // 本条评论回复的用户，根评论下为空
    private Long replyUserId;

    // 本条评论所在的评论分组的根评论所对应用户，根评论下为空
    private Long rootId;

    private Date createTime;

    private Date updateTime;

    // 如果本条评论为根评论里，需要保存其评论组下所有的评论，方便前端展示
    private List<VideoComment> childList;

    // 发表评论用户的信息
    private UserInfo userInfo;

    // 本条评论回复的用户的信息
    private UserInfo replyUserInfo;

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(Long replyUserId) {
        this.replyUserId = replyUserId;
    }

    public Long getRootId() {
        return rootId;
    }

    public void setRootId(Long rootId) {
        this.rootId = rootId;
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

    public List<VideoComment> getChildList() {
        return childList;
    }

    public void setChildList(List<VideoComment> childList) {
        this.childList = childList;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getReplyUserInfo() {
        return replyUserInfo;
    }

    public void setReplyUserInfo(UserInfo replyUserInfo) {
        this.replyUserInfo = replyUserInfo;
    }
}
