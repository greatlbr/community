package com.comm.community.dto;

import lombok.Data;

@Data
public class AccessTokenDto {//获取token
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
    private String scope;
    private String state;
}
