package com.yurie.miaosha.service;

import com.yurie.miaosha.error.BusinessException;
import com.yurie.miaosha.service.model.ItemModel;

import java.util.List;

public interface ItemService {
    // 创建商品
    ItemModel createItem(ItemModel itemModel) throws BusinessException;
    // 商品详情浏览
    ItemModel getItemById(Integer id);
    // 商品列表浏览
    List<ItemModel> listItem();
}
