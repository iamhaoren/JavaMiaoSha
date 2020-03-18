package com.rt.miaosha.service;

import com.rt.miaosha.error.BusinessException;
import com.rt.miaosha.service.model.UserModel;

public interface UserService {
    UserModel getUserById(Integer id);
    void register(UserModel userModel) throws BusinessException;
    /*
    telephone:用户手机号
    password:加密后的密码
     */
    UserModel validateLogin(String telephone,String encrptPassword) throws BusinessException;
}
