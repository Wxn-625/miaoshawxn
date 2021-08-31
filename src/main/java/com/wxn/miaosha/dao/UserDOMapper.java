package com.wxn.miaosha.dao;

import com.wxn.miaosha.dataobject.UserDO;
import org.springframework.stereotype.Repository;


public interface UserDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info
     *
     * @mbg.generated Sat Aug 28 10:55:49 CST 2021
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info
     *
     * @mbg.generated Sat Aug 28 10:55:49 CST 2021
     */
    int insert(UserDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info
     *
     * @mbg.generated Sat Aug 28 10:55:49 CST 2021
     */
    int insertSelective(UserDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info
     *
     * @mbg.generated Sat Aug 28 10:55:49 CST 2021
     */
    UserDO selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info
     *
     * @mbg.generated Sat Aug 28 10:55:49 CST 2021
     */
    int updateByPrimaryKeySelective(UserDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table user_info
     *
     * @mbg.generated Sat Aug 28 10:55:49 CST 2021
     */
    int updateByPrimaryKey(UserDO record);

    UserDO selectByUserTel(String telphone);
}