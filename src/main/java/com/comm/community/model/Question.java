package com.comm.community.model;

import lombok.Data;

@Data
public class Question {
    private Integer id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer creator;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;

//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//    public String getTag() {
//        return tag;
//    }
//
//    public void setTag(String tag) {
//        this.tag = tag;
//    }
//
//    public Long getGmt_create() {
//        return gmt_create;
//    }
//
//    public void setGmt_create(Long gmt_create) {
//        this.gmt_create = gmt_create;
//    }
//
//    public Long getGmt_modified() {
//        return gmt_modified;
//    }
//
//    public void setGmt_modified(Long gmt_modified) {
//        this.gmt_modified = gmt_modified;
//    }
//
//    public Integer getCreator() {
//        return creator;
//    }
//
//    public void setCreator(Integer creator) {
//        this.creator = creator;
//    }
//
//    public Integer getViewCount() {
//        return viewCount;
//    }
//
//    public void setViewCount(Integer viewCount) {
//        this.viewCount = viewCount;
//    }
//
//    public Integer getCommentCount() {
//        return commentCount;
//    }
//
//    public void setCommentCount(Integer commentCount) {
//        this.commentCount = commentCount;
//    }
//
//    public Integer getLikeCount() {
//        return likeCount;
//    }
//
//    public void setLikeCount(Integer likeCount) {
//        this.likeCount = likeCount;
//    }
}
