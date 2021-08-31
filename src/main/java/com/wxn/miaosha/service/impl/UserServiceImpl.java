package com.wxn.miaosha.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.wxn.miaosha.dao.UserDOMapper;
import com.wxn.miaosha.dao.UserPasswordDOMapper;
import com.wxn.miaosha.dataobject.UserDO;
import com.wxn.miaosha.dataobject.UserPasswordDO;
import com.wxn.miaosha.error.BussinessException;
import com.wxn.miaosha.error.EmBusinessError;
import com.wxn.miaosha.service.UserService;
import com.wxn.miaosha.service.model.UserModel;
import com.wxn.miaosha.validator.ValidationResult;
import com.wxn.miaosha.validator.ValidatorImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    UserPasswordDOMapper userPasswordDOMapper;

    @Autowired
    ValidatorImpl validator;

    @Override
    public UserModel getUserById(Integer id) {
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(id);

        return trans(userDO,userPasswordDO);
    }

    @Override
    @Transactional
    public void register(UserModel userModel) throws BussinessException{
        if(userModel == null)
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);

//        if(StringUtils.isEmpty(userModel.getName())
//                || userModel.getGender() == null
//                || userModel.getAge() == null
//                || StringUtils.isEmpty(userModel.getTelphone()))
//                throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        ValidationResult result = validator.validate(userModel);
        if(result.isHasErrors()){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }

        UserDO userDO = convertFromModel(userModel);
        // insertSelective相对于insert方法，不会覆盖掉数据库的默认值
        try{
            userDOMapper.insertSelective(userDO);
        }
        catch (DuplicateKeyException ex){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"手机号已存在");
        }

        userModel.setId(userDO.getId());

        UserPasswordDO userPasswordDO = convertPasswordFromModel(userModel);
        System.out.println(userPasswordDO);

        userPasswordDOMapper.insertSelective(userPasswordDO);
        System.out.println(userPasswordDO);

        return;

    }

    @Override
    public UserModel login(String telphone, String password) throws BussinessException {
        if(StringUtils.isEmpty(telphone)||
                StringUtils.isEmpty(password)){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        UserDO userDO = userDOMapper.selectByUserTel(telphone);
        if(userDO == null){
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"用户不存在");
        }

        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        if(userPasswordDO == null)
            throw new BussinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"数据库不一致");

        return trans(userDO,userPasswordDO);
    }

    private UserPasswordDO convertPasswordFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userModel.getEncrptPassword());
        userPasswordDO.setUserId(userModel.getId());

        return userPasswordDO;
    }

    private UserDO convertFromModel(UserModel userModel) {
        if (userModel == null) {
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userModel, userDO);
        return userDO;
    }


    public UserModel trans(UserDO userDO, UserPasswordDO userPasswordDO){
        if(userDO == null)
            return null;

        UserModel userModel = new UserModel();
        BeanUtils.copyProperties(userDO,userModel);

        if(userPasswordDO != null)
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());

        return userModel;
    }
}
