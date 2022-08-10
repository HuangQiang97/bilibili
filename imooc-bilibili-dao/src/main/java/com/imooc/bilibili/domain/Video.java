package com.imooc.bilibili.domain;


import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 * 视频基本信息
 *
 * @author huangqiang
 * @date 2022/4/15 15:22
 * @see
 * @since
 */

//指示如何将对象转换为文档，指定的索引名称用于es查询
@Document(indexName = "videos")
public class Video {

    // 主键
    @Id
    private Long id;

    @Field(type = FieldType.Long)
    private Long userId;//用户id

    private String url; //视频链接

    private String thumbnail;//封面

    //text类型，可被分词，将查询语句拆分为多个关键词，实现部分匹配，扩大搜索结果范围
    @Field(type = FieldType.Text)
    private String title; //标题

    private String type;// 0自制 1转载

    private String duration;//时长

    private String area;//分区

    private List<VideoTag> videoTagList;//标签列表

    //text类型，可被分词，将查询语句拆分为多个关键词，实现部分匹配，扩大搜索结果范围
    @Field(type = FieldType.Text)
    private String description;//简介

    @Field(type = FieldType.Date)
    private Date createTime;
    
    @Field(type = FieldType.Date)
    private Date updateTime;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public List<VideoTag> getVideoTagList() {
        return videoTagList;
    }

    public void setVideoTagList(List<VideoTag> videoTagList) {
        this.videoTagList = videoTagList;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
