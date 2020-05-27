package com.yurie.miaosha.service;

import com.yurie.miaosha.error.BusinessException;
import com.yurie.miaosha.service.model.OrderModel;

public interface OrderService {
    // 创建订单
    OrderModel createOrder(Integer userId, Integer itemId, Integer promoId, Integer amount, String stockLodId) throws BusinessException;
}
