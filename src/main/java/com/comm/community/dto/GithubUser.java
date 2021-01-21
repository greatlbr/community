package com.comm.community.dto;

import lombok.Data;

@Data
public class GithubUser {
    private String name;
    private Long id;
    private String bio;
    private String avatar_url;
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    public void setId(long id) {
//        this.id = id;
//    }
//
//    public String getBis() {
//        return bis;
//    }
//
//    public void setBis(String bis) {
//        this.bis = bis;
//    }
//
//    @Override
//    public String toString() {
//        return "GithubUser{" +
//                "name='" + name + '\'' +
//                ", id=" + id +
//                ", bis='" + bis + '\'' +
//                '}';
//    }
}
