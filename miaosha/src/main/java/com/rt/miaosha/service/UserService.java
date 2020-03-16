package com.rt.miaosha.service;

import com.rt.miaosha.service.model.UserModel;

public interface UserService {
    UserModel getUserById(Integer id);
    void register(UserModel userModel);
}
