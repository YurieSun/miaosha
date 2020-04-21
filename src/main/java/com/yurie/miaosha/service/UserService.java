package com.yurie.miaosha.service;

import com.yurie.miaosha.dataobject.UserDO;
import com.yurie.miaosha.error.BusinessException;
import com.yurie.miaosha.service.model.UserModel;

public interface UserService {
    // 通过用户id获取用户对象
    UserModel getUserById(Integer id);
    // 用户注册
    UserDO register(UserModel userModel) throws BusinessException;
    // 用户登陆
    UserModel validateLogin(String telphone, String encrptPassword) throws BusinessException;
}
