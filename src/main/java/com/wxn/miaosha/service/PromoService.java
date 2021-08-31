package com.wxn.miaosha.service;

import com.wxn.miaosha.service.model.PromoModel;
import org.apache.ibatis.annotations.Param;

public interface PromoService {
    PromoModel getPromoByItemId(Integer itemId);
}
