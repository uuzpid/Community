package com.pyx.community.provider;

import com.alibaba.fastjson.JSON;
import com.pyx.community.dto.AccessTokenDTO;
import com.pyx.community.dto.GithubUser;
import okhttp3.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubProvider {
    public String getAccessToken(AccessTokenDTO accessTokenDTO) {
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON.toJSONString(accessTokenDTO),mediaType);
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token?client_id="+accessTokenDTO.getClient_id()+"&client_secret="
                        +accessTokenDTO.getClient_secret()+"&code="+accessTokenDTO.getCode()+"&redirect_uri="+
                        accessTokenDTO.getRedirect_uri()+"&state="+accessTokenDTO.getState())
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()){
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
                //github新版请求方式
                .url("https://api.github.com/user?"+accessToken)
//               .url("https://api.github.com/user?access_token=" + accessToken)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (IOException e) {
        }

        return null;
    }
}

