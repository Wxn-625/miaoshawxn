package com.wxn.miaosha.service.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemModel {
    private Integer id;

    // 商品名称
    @NotBlank(message = "商品名不能为空")
    private String title;

    // 商品价格
    @NotNull(message = "价格不能为空")
    @Min(value = 0,message = "价格不能小于0")
    private BigDecimal price;

    // 商品库存
    @NotNull(message = "库存不能为空")
    @Min(value = 0,message = "库存不能小于0")
    private Integer stock;

    // 商品描述
    @NotNull(message = "描述信息不能为空")
    private String description;

    // 商品销量
    private Integer sales;

    // 商品引用的图片
    @NotNull(message = "图片信息不能为空")
    private String imgUrl;

    // 如果promoModel不为空，则表示有未结束的秒杀活动
    private PromoModel promoModel;
}
