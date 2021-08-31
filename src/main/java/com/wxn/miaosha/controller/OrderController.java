package com.wxn.miaosha.controller;

import com.wxn.miaosha.dataobject.OrderDO;
import com.wxn.miaosha.error.BussinessException;
import com.wxn.miaosha.error.EmBusinessError;
import com.wxn.miaosha.response.CommonReturnType;
import com.wxn.miaosha.service.OrderService;
import com.wxn.miaosha.service.model.OrderModel;
import com.wxn.miaosha.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.relational.core.sql.In;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller("/order")
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true",originPatterns = "*",allowedHeaders = "*")
public class OrderController extends BaseController{

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    // 封装下单请求
    @RequestMapping(value = "/createorder",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name="itemId")Integer itemId,
                                        @RequestParam(name = "amount")Integer amount,
                                        @RequestParam(name="promoId",required = false)Integer promoId) throws BussinessException {
        // 获取用户的登录信息
        Boolean is_login = (Boolean)httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if(is_login == null|| !is_login)
            throw new BussinessException(EmBusinessError.USER_NOT_LOGIN);
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");


        OrderModel orderModel = orderService.createOrder(userModel.getId(), itemId,promoId, amount);
        return CommonReturnType.create(orderModel);
    }
}
