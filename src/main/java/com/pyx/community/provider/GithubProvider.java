package com.pyx.community.provider;

import com.alibaba.fastjson.JSON;
import com.pyx.community.dto.AccessTokenDTO;
import com.pyx.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubProvider {
    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON.toJSONString(accessTokenDTO),mediaType);
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            //ctrl+alt+n可以快速将变量放到原文中去
            //获取到的access_token格式
            //access_token=1da71bac7b763504440606e6891db40ac6e3f670&scope=user&token_type=bearer
            String token = string.split("&")[0].split("=")[1];
            System.out.println(token);
            return string;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public GithubUser githubUser(String accessToken) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.github.com/user?" + accessToken)
                .build();
        //会自动拼接成https://api.github.com/user?accessToken=
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            //用fastjson工具将String类型对象转换成GithubUser类型
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

