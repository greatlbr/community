package com.comm.community.controller;

import com.comm.community.dto.AccessTokenDto;
import com.comm.community.dto.GithubUser;
import com.comm.community.mapper.UserMapper;
import com.comm.community.model.User;
import com.comm.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;
//import org.springframework.ui.Model;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.client.id}")
    private String clientId;

    @Value("${github.Client.secret}")
    private String clientSecret;

    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletRequest request,
                           HttpServletResponse response){
        AccessTokenDto accessTokenDto = new AccessTokenDto();
        accessTokenDto.setClient_id(clientId);
        accessTokenDto.setClient_secret(clientSecret);
        accessTokenDto.setCode(code);
        accessTokenDto.setRedirect_uri(redirectUri);
        accessTokenDto.setState(state);
        String accessToken = githubProvider.getAccessToken(accessTokenDto);
        GithubUser githubUser = githubProvider.getUser(accessToken);//使用github登陆成功
        System.out.println(githubUser.getName());
        if (githubUser != null && githubUser.getId() != null){
            User user = new User();//获取用户信息
            String token = UUID.randomUUID().toString();//获取用户信息时生成token
            user.setToken(token);//将token放到user类对象里
            user.setName(githubUser.getName());//-->存储到数据库
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setAvatarUrl(githubUser.getAvatar_url());//<--
            userMapper.insert(user);
            response.addCookie(new Cookie("token", token));//把token放到cookie里
            //登陆成功， 写cookie 和session
            return "redirect:/";//已经写入token，所以访问首页时需要把Cookie里key为token的信息拿到，然后去数据库中查询看数据库中是否存在，以此来验证是否登录成功
        }else{
            //重新登陆
            return "redirect:/";
        }
    }
}
