package com.wxn.miaosha.service.impl;

import com.wxn.miaosha.dao.ItemDOMapper;
import com.wxn.miaosha.dao.ItemStockDOMapper;
import com.wxn.miaosha.dataobject.ItemDO;
import com.wxn.miaosha.dataobject.ItemStockDO;
import com.wxn.miaosha.dataobject.UserDO;
import com.wxn.miaosha.error.BussinessException;
import com.wxn.miaosha.error.EmBusinessError;
import com.wxn.miaosha.response.CommonReturnType;
import com.wxn.miaosha.service.ItemService;
import com.wxn.miaosha.service.PromoService;
import com.wxn.miaosha.service.model.ItemModel;
import com.wxn.miaosha.service.model.PromoModel;
import com.wxn.miaosha.service.model.UserModel;
import com.wxn.miaosha.validator.ValidationResult;
import com.wxn.miaosha.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private ItemStockDOMapper  itemStockDOMapper;

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private PromoService promoService;


    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BussinessException {
        // 校验入参
        ValidationResult result = validator.validate(itemModel);
        if(result.isHasErrors()){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }

        // 转化itemmodel->dataobject
        ItemDO itemDO = convertItemDOFromModel(itemModel);

        // 写入数据库
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());

        ItemStockDO itemStockDO = convertItemStockDOFromModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);

        // 返回创建完成的对象
        return this.getItemById(itemModel.getId());
    }

    private ItemDO convertItemDOFromModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel, itemDO);
        // 因为数据库是double,而模型中是Decimal，传到前端时可能会出现精度问题。
        itemDO.setPrice(itemModel.getPrice().doubleValue());
        return itemDO;
    }

    private ItemStockDO convertItemStockDOFromModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemStockDO itemStockDO = new ItemStockDO();

        itemStockDO.setStock(itemModel.getStock());
        itemStockDO.setItemId(itemModel.getId());

        return itemStockDO;
    }

    @Override
    public List<ItemModel> listItem() {

        List<ItemDO> itemDOS = itemDOMapper.selectAllItemDO();
        List<ItemModel> itemModels = new ArrayList<>();

        itemDOS.forEach(itemDO -> {
            // 找到对应的库存
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
            // 加入数组
            itemModels.add(convertModelFromDO(itemDO,itemStockDO));
        });

        return itemModels;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if(itemDO == null)
            return null;

        // 获取库存数量
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(id);

        // 获取秒杀活动信息
        PromoModel promoModel = promoService.getPromoByItemId(id);


        // 将dataobject -> model

        ItemModel itemModel = convertModelFromDO(itemDO, itemStockDO);

        // 如果存在秒杀活动，且未结束，就设置
        if(promoModel != null && promoModel.getStatus() != 3)
            itemModel.setPromoModel(promoModel);

        return itemModel;
    }

    @Override
    @Transactional
    public boolean decreseStock(Integer itemId, Integer amount) throws BussinessException {
        // affectRow 表示修改影响的条目数
        int affectRow = itemStockDOMapper.decreseStock(itemId,amount);

        // 更新成功
        if(affectRow > 0){
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) throws BussinessException {
        itemDOMapper.increaseSales(itemId,amount);
    }

    private ItemModel convertModelFromDO(ItemDO itemDO,ItemStockDO itemStockDO){
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO,itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());

        return itemModel;
    }
}
