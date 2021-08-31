package com.wxn.miaosha.controller;

import com.wxn.miaosha.controller.vieobject.ItemVO;
import com.wxn.miaosha.error.BussinessException;
import com.wxn.miaosha.response.CommonReturnType;
import com.wxn.miaosha.service.ItemService;
import com.wxn.miaosha.service.model.ItemModel;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Controller("/item")
@RequestMapping("/item")
@CrossOrigin(allowCredentials = "true",originPatterns = "*",allowedHeaders = "*")
public class ItemController extends BaseController{


    @Autowired
    ItemService itemService;

    // 创建商品
    @RequestMapping(value = "/create",method = {RequestMethod.POST},consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType create(@RequestParam(name = "title")String title,
                                   @RequestParam(name = "price")BigDecimal price,
                                   @RequestParam(name = "description")String description,
                                   @RequestParam(name = "stock")Integer stock,
                                   @RequestParam(name = "imgUrl")String imgUrl) throws BussinessException {

        // 封装Service请求来创建商品
        ItemModel itemModel = new ItemModel();
        itemModel.setPrice(price);
        itemModel.setStock(stock);
        itemModel.setDescription(description);
        itemModel.setImgUrl(imgUrl);
        itemModel.setTitle(title);

        ItemModel itemModelForReturn = itemService.createItem(itemModel);

        // 将信息返回给前端
        ItemVO itemVO = convertItemVOFromItemModel(itemModelForReturn);

        return CommonReturnType.create(itemVO);
    }

    private ItemVO convertItemVOFromItemModel(ItemModel itemModel){
        if(itemModel == null)
            return null;

        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel,itemVO);

        if(itemModel.getPromoModel() != null){
            //有正在进行或即将进行的秒杀活动
            itemVO.setPromoStatus(itemModel.getPromoModel().getStatus());
            itemVO.setPromoId(itemModel.getPromoModel().getId());
            itemVO.setStartDate(itemModel.getPromoModel().getStartDate().toString(DateTimeFormat.forPattern("yyyy-mm-dd hh:mm:ss")));
            itemVO.setPromoPrice(itemModel.getPromoModel().getPromoItemPrice());
        }else{
            itemVO.setPromoStatus(0);
        }
        return itemVO;
    }


    // 列举所有商品
    @RequestMapping(value = "/listAll",method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType listAll(){
        List<ItemModel> itemModels = itemService.listItem();

        // 使用stream api将list内的itemModel转化成ItemVO
        List<ItemVO> itemVOS = itemModels.stream().map(itemModel -> {
            ItemVO itemVO = this.convertItemVOFromItemModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());

        return CommonReturnType.create(itemVOS);
    }

    // 商品详情页浏览
    @RequestMapping(value = "/get",method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType list(@RequestParam(name = "id")Integer id){
        System.out.println("ggggg");
        ItemModel itemModel = itemService.getItemById(id);
        return CommonReturnType.create(convertItemVOFromItemModel(itemModel));
    }
}
