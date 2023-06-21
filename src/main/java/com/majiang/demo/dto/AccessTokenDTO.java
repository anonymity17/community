package com.majiang.demo.dto;

import lombok.Data;

//网络中的
//dto数据传输模型，超过五个参数传输，封装
@Data
public class AccessTokenDTO {
    private String client_id;
    private String client_secret;
    private String code;
    private String redirect_uri;
    private String state;
}
