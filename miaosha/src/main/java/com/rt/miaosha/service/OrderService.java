package com.rt.miaosha.service;

import com.rt.miaosha.error.BusinessException;
import com.rt.miaosha.service.model.OrderModel;

public interface OrderService {
    OrderModel createOrder(Integer userId,Integer itemId,Integer amount) throws BusinessException;

}
