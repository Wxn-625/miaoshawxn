package com.wxn.miaosha.service.impl;


import com.wxn.miaosha.dao.OrderDOMapper;
import com.wxn.miaosha.dao.SequenceDOMapper;
import com.wxn.miaosha.dataobject.OrderDO;
import com.wxn.miaosha.dataobject.SequenceDO;
import com.wxn.miaosha.error.BussinessException;
import com.wxn.miaosha.error.EmBusinessError;
import com.wxn.miaosha.service.ItemService;
import com.wxn.miaosha.service.OrderService;
import com.wxn.miaosha.service.UserService;
import com.wxn.miaosha.service.model.ItemModel;
import com.wxn.miaosha.service.model.OrderModel;
import com.wxn.miaosha.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private UserService userService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private OrderDOMapper orderDOMapper;

    @Autowired
    private SequenceDOMapper sequenceDOMapper;

    @Override
    @Transactional
    public OrderModel createOrder(Integer userId, Integer itemId,Integer promoId, Integer amount) throws BussinessException {
        // 1.校验下单状态，下单商品是否存在，用户是否合法，购买数量是否正确
        ItemModel itemModel = itemService.getItemById(itemId);
        UserModel userModel = userService.getUserById(userId);
        if(itemModel == null || userModel == null ){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"下单状态错误");
        }
        if(itemModel.getStock() < amount || amount < 0){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"库存不足");
        }

        // 校验活动信息
        if(promoId != null){
             // （1）校验对应活动是否适用于该商品
            if(promoId.intValue() != itemModel.getPromoModel().getId())
                throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动信息错误");
            else if(itemModel.getPromoModel().getStatus().intValue() != 2){
                throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"活动未开始");
            }
        }

        // 2.落单减库存，支付减库存
        boolean result = itemService.decreseStock(itemId, amount);
        if(!result){
            throw new BussinessException(EmBusinessError.STOCK_NOT_ENOUGH);
        }

        // 3.订单入库
        OrderModel orderModel = new OrderModel();
        orderModel.setItemId(itemId);
        orderModel.setAmount(amount);
        orderModel.setUserId(userId);
        if(promoId != null)
            orderModel.setItemPrice(itemModel.getPromoModel().getPromoItemPrice());
        else
            orderModel.setItemPrice(itemModel.getPrice());
        orderModel.setPromoId(promoId);
        orderModel.setOrderPrice(itemModel.getPrice().multiply(new BigDecimal(amount)));

        // 生成交易流水号
        orderModel.setId(generateOrderNo());

        OrderDO orderDO = convertOrderDOFromModel(orderModel);
        orderDOMapper.insertSelective(orderDO);

        // 加上商品销量
        itemService.increaseSales(itemId,amount);

        // 4.返回前端
        return orderModel;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
     public String generateOrderNo(){
        // 订单号有16位
        StringBuilder stringBuilder = new StringBuilder();
        // 前8位为时间信息，年月日
        LocalDateTime now = LocalDateTime.now();
        String nowDate = now.format(DateTimeFormatter.ISO_DATE).replace("-", "");
        stringBuilder.append(nowDate);

        // 中间6位为自增序列
        SequenceDO sequenceDO = sequenceDOMapper.selectByName("order_sequence");
        Integer currentValue = sequenceDO.getCurrentValue();
        sequenceDO.setCurrentValue(sequenceDO.getCurrentValue() + sequenceDO.getStep());
        sequenceDOMapper.updateByPrimaryKey(sequenceDO);
        String sequenceStr = String.valueOf(currentValue);
        for(int i = 0;i < 6 - sequenceStr.length();i++){
            stringBuilder.append("0");
        }
        stringBuilder.append(sequenceStr);


        // 最后2位为分库分表位,暂时写死
        stringBuilder.append("00");

        return stringBuilder.toString();
    }

    private OrderDO convertOrderDOFromModel(OrderModel orderModel){
        if(orderModel == null)
            return null;

        OrderDO orderDO = new OrderDO();
        BeanUtils.copyProperties(orderModel,orderDO);
        orderDO.setItemPrice(orderModel.getItemPrice().doubleValue());
        orderDO.setOrderPrice(orderModel.getOrderPrice().doubleValue());
        return orderDO;
    }
}
