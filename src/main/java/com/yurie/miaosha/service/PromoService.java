package com.yurie.miaosha.service;

import com.yurie.miaosha.service.model.PromoModel;

public interface PromoService {
    // 根据商品id获取秒杀活动
    PromoModel getPromoByItemId(Integer itemId);
}
