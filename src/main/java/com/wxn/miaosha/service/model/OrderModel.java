package com.wxn.miaosha.service.model;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class OrderModel {
    //2018102100012828
    private String id;

    // 购买用户的id
    private Integer userId;

    // 购买商品的id
    private Integer itemId;

    // 若非空则表示是以秒杀商品方式下单
    private Integer promoId;

    // 购买商品当时的单价
    private BigDecimal itemPrice;

    // 购买数量
    private Integer amount;

    // 购买金额
    private BigDecimal orderPrice;
}
