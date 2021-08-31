package com.wxn.miaosha.service.impl;

import com.wxn.miaosha.dao.PromoDOMapper;
import com.wxn.miaosha.dataobject.PromoDO;
import com.wxn.miaosha.service.PromoService;
import com.wxn.miaosha.service.model.PromoModel;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class PromoServiceImpl implements PromoService {

    @Autowired
    PromoDOMapper promoDOMapper;

    @Override
    public PromoModel getPromoByItemId(Integer itemId) {
        PromoDO promoDO = promoDOMapper.selectByItemId(itemId);

        PromoModel promoModel = convertPromoModelFromPromoDO(promoDO);
        if(promoModel == null)
            return null;

        // 判断目前秒杀活动是否开始
        DateTime now = DateTime.now();
        if(promoModel.getStartDate().isAfterNow()){
            promoModel.setStatus(1);
        }else if(promoModel.getEndDate().isBeforeNow()){
            promoModel.setStatus(3);
        }else{
            promoModel.setStatus(2);
        }

        return promoModel;
    }

    private PromoModel convertPromoModelFromPromoDO(PromoDO promoDO){
        if(promoDO == null)
            return null;

        PromoModel promoModel = new PromoModel();
        BeanUtils.copyProperties(promoDO,promoModel);
        promoModel.setPromoItemPrice(new BigDecimal(promoDO.getPromoItemPrice()));
        promoModel.setStartDate(new DateTime(promoDO.getStartDate()));
        promoModel.setEndDate(new DateTime(promoDO.getEndDate()));


        return promoModel;
    }

}
