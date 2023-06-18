package com.majiang.demo.provider;

import com.majiang.demo.dto.AccessTokenDTO;
import com.majiang.demo.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import com.alibaba.fastjson.JSON;

@Component
public class GithubProvider {
    public String getAccessToken(AccessTokenDTO accessTokenDTO){
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");

        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(mediaType,JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
//            这里获取github返回的token（1.2.1.2）
            String str = response.body().string();
            //这个str输出为下面：
            //access_token=ghu_bgQ67sY3NQ3ZRJzpAnB2zncHHUGFlL2juN12&expires_in=28800&refresh_token=ghr_l58QEPLA52PyhwucVb8EGOfQcVbYCRXwsFIIoZLCVFfPHQyRBPbxcyYRiWTy9tbbquYvD60UzryD&refresh_token_expires_in=15811200&scope=&token_type=bearer
            //根据这个结果，获取token
            String[] split = str.split("&");
            String tokenStr = split[0];//access_token=ghu_bgQ67sY3NQ3ZRJzpAnB2zncHHUGFlL2juN12&expires_in=28800
            String token = tokenStr.split("=")[1];

            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.github.com/user")
                .header("Authorization","token " + accessToken)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String str = response.body().string();
//            自动将将str这样一个json对象解析成GithubUser这样的java的类对象
            GithubUser githubUser = JSON.parseObject(str,GithubUser.class);
            return githubUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
