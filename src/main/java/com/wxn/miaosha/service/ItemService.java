package com.wxn.miaosha.service;

import com.wxn.miaosha.error.BussinessException;
import com.wxn.miaosha.service.model.ItemModel;

import java.util.List;

public interface ItemService {

    // 创建商品
    ItemModel createItem(ItemModel itemModel) throws BussinessException;

    // 商品列表浏览
    List<ItemModel> listItem();

    // 商品详情浏览
    ItemModel getItemById(Integer id);

    // 库存扣减
    boolean decreseStock(Integer itemId,Integer amount)throws BussinessException;

    // 商品销量增加
    void increaseSales(Integer itemId,Integer amount) throws BussinessException;

}
