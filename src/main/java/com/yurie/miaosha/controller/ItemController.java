package com.yurie.miaosha.controller;

import com.yurie.miaosha.controller.viewobject.ItemVO;
import com.yurie.miaosha.error.BusinessException;
import com.yurie.miaosha.response.CommonReturnType;
import com.yurie.miaosha.service.ItemService;
import com.yurie.miaosha.service.model.ItemModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller("item")
@RequestMapping("/item")
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class ItemController extends BaseController {
    @Autowired
    private ItemService itemService;

    // 创建商品接口
    @RequestMapping(value = "/create", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createItem(@RequestParam(name = "title") String title,
                                       @RequestParam(name = "price") BigDecimal price,
                                       @RequestParam(name = "description") String description,
                                       @RequestParam(name = "stock") Integer stock,
                                       @RequestParam(name = "imgUrl") String imgUrl) throws BusinessException {
        ItemModel itemModel = new ItemModel();
        itemModel.setTitle(title);
        itemModel.setPrice(price);
        itemModel.setDescription(description);
        itemModel.setStock(stock);
        itemModel.setImgUrl(imgUrl);

        ItemModel item = itemService.createItem(itemModel);
        ItemVO itemVO = convertItemVOFromItemModel(item);
        return CommonReturnType.create(itemVO);
    }

    // 浏览商品接口
    @RequestMapping(value = "/get", method = {RequestMethod.GET})
    @ResponseBody
    public CommonReturnType getItemById(@RequestParam(name = "id") Integer id) {
        ItemModel itemModel = itemService.getItemById(id);
        ItemVO itemVO = convertItemVOFromItemModel(itemModel);
        return CommonReturnType.create(itemVO);
    }

    // itemModel->itemVO
    private ItemVO convertItemVOFromItemModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel, itemVO);
        return itemVO;
    }
}
