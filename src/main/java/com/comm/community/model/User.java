package com.comm.community.model;

import lombok.Data;

@Data
public class User {
    private Integer id;
    private String name;
    private String accountId;
    private String token;
    private Long gmtCreate;
    private Long gmtModified;
    private String avatarUrl;

//    public Integer getId() {
//        return id;
//    }
//
//    public void setId(Integer id) {
//        this.id = id;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getAccountId() {
//        return accountId;
//    }
//
//    public void setAccountId(String accountId) {
//        this.accountId = accountId;
//    }
//
//    public String getToken() {
//        return token;
//    }
//
//    public void setToken(String token) {
//        this.token = token;
//    }
//
//    public Long getGmtCreate() {
//        return gmtCreate;
//    }
//
//    public void setGmtCreate(Long gmtCreate) {
//        this.gmtCreate = gmtCreate;
//    }
//
//    public Long getGmtModified() {
//        return gmtModified;
//    }
//
//    public void setGmtModified(Long gmtModified) {
//        this.gmtModified = gmtModified;
//    }
//
//    public String getAvatar_url() {
//        return avatar_url;
//    }
//
//    public void setAvatar_url(String avatar_url) {
//        this.avatar_url = avatar_url;
//    }
}
