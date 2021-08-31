package com.wxn.miaosha.controller;

import com.alibaba.druid.util.StringUtils;


import com.wxn.miaosha.controller.vieobject.UserVO;
import com.wxn.miaosha.error.BussinessException;
import com.wxn.miaosha.error.EmBusinessError;
import com.wxn.miaosha.response.CommonReturnType;
import com.wxn.miaosha.service.UserService;
import com.wxn.miaosha.service.model.UserModel;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Base64;
import java.util.Random;

@Controller
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true",originPatterns = "*",allowedHeaders = "*")
public class UserController extends BaseController{

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;


    // 用户校验
    @RequestMapping(value = "/login",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telphone") String telphone,
                                  @RequestParam(name = "password") String password) throws BussinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        // 入参校验
        if(StringUtils.isEmpty(telphone)||
        StringUtils.isEmpty(password)){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        // 获取内容
        UserModel userModel = userService.login(telphone, password);

        // 验证
        String encodePassword = encodeByMd5(password);
        if(StringUtils.equals(encodePassword,userModel.getEncrptPassword())){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"密码错误");
        }

        // 将登录凭证加入到用户登录成功的session中
        this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
        this.httpServletRequest.getSession().setAttribute("LOGIN_USER",userModel);

        return CommonReturnType.create(null);

    }



    // 用户注册
    @RequestMapping(value = "/register",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telphone") String telphone,
                                     @RequestParam(name = "otpCode")String otpCode,
                                     @RequestParam(name = "name")String name,
                                     @RequestParam(name = "gender")String gender,
                                     @RequestParam(name = "age")String age,
                                     @RequestParam(name = "password")String password) throws BussinessException, UnsupportedEncodingException, NoSuchAlgorithmException {

        // 验证手机验证码是否正确
        String inSessionOtpCode = (String)this.httpServletRequest.getSession().getAttribute(telphone);
        if(!StringUtils.equals(otpCode,inSessionOtpCode)){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"验证码错误");
        }



        // 用户注册流程
        UserModel userModel = new UserModel();
        userModel.setName(name);
        userModel.setAge(Integer.valueOf(age));
        userModel.setGender(Byte.valueOf(gender));
        userModel.setRegisterMode("byphone");
        userModel.setTelphone(telphone);

        // 密码加密
        userModel.setEncrptPassword(encodeByMd5(password));
        userService.register(userModel);
        return CommonReturnType.create(null);

    }

    //密码加密
    public static String encodeByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        Base64.Encoder encoder = Base64.getEncoder();
        //加密字符串
        String newstr = encoder.encodeToString(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }


    @RequestMapping(value = "/getotp",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOpt(@RequestParam(name="telphone") String telphone){
        System.out.println(telphone);
        // 按照一定的规则生成optcode
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 100000;
        String otpCode = String.valueOf(randomInt);

        // 将optCode与telphone关联，一般存在redis中，但是这里用http session。
        httpServletRequest.getSession().setAttribute(telphone,otpCode);

        // 发送OTP验证码，此处省略
        System.out.println("tel" + telphone + "otpCode" + httpServletRequest.getSession().getAttribute(telphone));

        return CommonReturnType.create("adad");

    }


    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id") Integer id) throws BussinessException {
        UserModel userM = userService.getUserById(id);

        if(userM == null){
            throw new BussinessException(EmBusinessError.USER_NOT_EXIST);
        }
        // 将核心领域模型用户对象转化为可供UI使用的viewobject
        UserVO userVO = tramFronModel(userM);
        return CommonReturnType.create(userVO);
    }

    private UserVO tramFronModel(UserModel userModel){
        if(userModel == null){
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userModel,userVO);

        return userVO;
    }


}
