package com.majiang.demo.dto;

import com.majiang.demo.exception.CustomizeErrorCode;
import com.majiang.demo.exception.CustomizeException;
import lombok.Data;
//将返回的结果封装
@Data
public class ResultDTO {
    private Integer code;
    private String message;

    public static ResultDTO errorOf(Integer code, String message){
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(code);
        resultDTO.setMessage(message);
        return resultDTO;
    }

    public static ResultDTO errorOf(CustomizeErrorCode errorCode) {
        return errorOf(errorCode.getCode(),errorCode.getMessage());
    }
    /**
     * 将异常里面的信息分装成JSON的格式
     * @param e
     * @return
     */
    public static ResultDTO errorOf(CustomizeException e) {
        return errorOf(e.getCode(),e.getMessage());
    }

    public static ResultDTO okOf() {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setCode(200);
        resultDTO.setMessage("请求成功");
        return resultDTO;
    }


}
