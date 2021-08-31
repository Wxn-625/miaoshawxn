package com.wxn.miaosha.dao;

import com.wxn.miaosha.dataobject.PromoDO;
import com.wxn.miaosha.service.model.PromoModel;
import org.apache.ibatis.annotations.Param;

public interface PromoDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Mon Aug 30 23:44:15 CST 2021
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Mon Aug 30 23:44:15 CST 2021
     */
    int insert(PromoDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Mon Aug 30 23:44:15 CST 2021
     */
    int insertSelective(PromoDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Mon Aug 30 23:44:15 CST 2021
     */
    PromoDO selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Mon Aug 30 23:44:15 CST 2021
     */
    int updateByPrimaryKeySelective(PromoDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table promo
     *
     * @mbg.generated Mon Aug 30 23:44:15 CST 2021
     */
    int updateByPrimaryKey(PromoDO record);

    PromoDO selectByItemId(@Param("itemId")Integer itemId);
}