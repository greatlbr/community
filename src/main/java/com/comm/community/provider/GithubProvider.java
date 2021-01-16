package com.comm.community.provider;

import com.alibaba.fastjson.JSON;
import com.comm.community.dto.AccessTokenDto;
import com.comm.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

//Github第三方登陆

//最新github要求accesstoken发送必须要bearer
//查阅okhttp如何发送bearer token


/*
*okhttp:Get a URL
*
* OkHttpClient client = new OkHttpClient();
* String run(String url) throws IOException {
*  Request request = new Request.Builder()
*      .url(url)
*      .build();
*
*  try (Response response = client.newCall(request).execute()) {
*    return response.body().string();
*  }
*}
*/

/*
*okhttp:Post to a Server
*
*public static final MediaType JSON
*    = MediaType.get("application/json; charset=utf-8");
*
*OkHttpClient client = new OkHttpClient();
*
*String post(String url, String json) throws IOException {
*  RequestBody body = RequestBody.create(json, JSON);
*  Request request = new Request.Builder()
*      .url(url)
*      .post(body)
*      .build();
*  try (Response response = client.newCall(request).execute()) {
*    return response.body().string();
*  }
*}
*/

@Component
public class GithubProvider {
    public String getAccessToken(AccessTokenDto accessTokenDto){
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDto));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            //String string = response.body().string();//拿到当前这个String对象
            //System.out.println(string);
            //string: access_token=3e2dbd2ebccecf9273bc87187135d6685297f605&expires_in=28800&refresh_token=r1.9f9946bf1db81f40f13d8c78dacf07a20bc1b030832d6f9668da413e8003a6950987efd79b8dc816&refresh_token_expires_in=15638400&scope=&token_type=bearer
            //这里的access_token可以不用拆，直接作为参数传入url就可以的
            //return response.body().string();
            //String token = string.split("&").split("=")[1];
            //return token;

            String string = response.body().string();//拿到当前这个String对象
            String token = string.split("&")[0].split("=")[1];
            System.out.println(string);
            System.out.println(token);
            return token;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
        //RequestBody body = RequestBody.create(json, JSON);
        //Request request = new Request.Builder()
        //        .url("https://api.github.com/user?access_token=" + accessToken)  //6eb05748ba2dc38f626d954c7affab248c4c4422
        //        //.post(body)
        //        .build();

        Request request = new Request.Builder()
                //github新版请求方式
                .url("https://api.github.com/user")
                .header("Authorization","token "+accessToken)
//               .url("https://api.github.com/user?access_token=" + accessToken)
                .build();

        try {
            Response response = client.newCall(request).execute(); //拿到response
            String string = response.body().string();
            //已经知道了当前请求是json格式，所以直接使用fastjson的包来做解析
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);//将String的JSON对象自动转换成Java的类对象
            return githubUser;
        } catch (IOException e) {
        }
        return null;
    }
}
