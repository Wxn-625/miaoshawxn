package com.wxn.miaosha.service;

import com.wxn.miaosha.error.BussinessException;
import com.wxn.miaosha.service.model.UserModel;

public interface UserService {
    // 通过ID获取user的信息
    UserModel getUserById(Integer id);

    // 用户注册
    void register(UserModel userModel) throws BussinessException;

    // 用户登录
    UserModel login(String telphone,String password) throws BussinessException;
}
