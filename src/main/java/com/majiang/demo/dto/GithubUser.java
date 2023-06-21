package com.majiang.demo.dto;

import lombok.Data;

/**
 *  github返回的用户信息（1.2.1.4）
 *  返回的信息里面就是avatar_url；。。。
 *  所以这里是avatar_url，但是fastJSON可以自动的将avatar_url映射为avatarUrl
 */
@Data
public class GithubUser {
    private String name;
    private Long id;
    private String bio;
//    private String avatar_url;
    private String avatarUrl;
}
