package com.wxn.miaosha.controller;

import com.wxn.miaosha.error.BussinessException;
import com.wxn.miaosha.error.EmBusinessError;
import com.wxn.miaosha.response.CommonReturnType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class BaseController {
    public static final String CONTENT_TYPE_FORMED="application/x-www-form-urlencoded";


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Object handlerException(HttpServletRequest request, Exception ex){
        Map<String ,Object> responseData = new HashMap<>();

        if(ex instanceof BussinessException){
            BussinessException bussinessException = (BussinessException) ex;
            responseData.put("errCode",bussinessException.getErrCode());
            responseData.put("errMsg",bussinessException.getErrMsg());
        }else{
            responseData.put("errCode", EmBusinessError.UNKONWN_ERROR.getErrCode());
            responseData.put("errMsg",EmBusinessError.UNKONWN_ERROR.getErrMsg());
        }

        return CommonReturnType.create(responseData,"fail");
    }
}
