package com.rt.miaosha.service.impl;

import com.rt.miaosha.dao.UserDOMapper;
import com.rt.miaosha.dao.UserPasswordDOMapper;
import com.rt.miaosha.dataobject.UserDO;
import com.rt.miaosha.dataobject.UserPasswordDO;
import com.rt.miaosha.service.UserService;
import com.rt.miaosha.service.model.UserModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;

    @Override
    public UserModel getUserById(Integer id) {
        UserDO userDO=userDOMapper.selectByPrimaryKey(id);
        if (userDO==null)
            return null;
        //通过用户id获取用户加密密码信息
        UserPasswordDO userPasswordDO=userPasswordDOMapper.selectByUserId(userDO.getId());
        return convertFromDataObject(userDO,userPasswordDO);
    }

    @Override
    public void register(UserModel userModel) {

    }

    private UserModel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO){
        if (userDO==null)
            return null;
        UserModel userModel=new UserModel();
        BeanUtils.copyProperties(userDO,userModel);
        if (userPasswordDO!=null)
            userModel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        return userModel;
    }
}
