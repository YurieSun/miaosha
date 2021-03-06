package com.yurie.miaosha.service.impl;

import com.yurie.miaosha.dao.ItemDOMapper;
import com.yurie.miaosha.dao.ItemStockDOMapper;
import com.yurie.miaosha.dao.StockLogDOMapper;
import com.yurie.miaosha.dataobject.ItemDO;
import com.yurie.miaosha.dataobject.ItemStockDO;
import com.yurie.miaosha.dataobject.StockLogDO;
import com.yurie.miaosha.error.BusinessException;
import com.yurie.miaosha.error.EmBusinessError;
import com.yurie.miaosha.mq.MqProducer;
import com.yurie.miaosha.service.ItemService;
import com.yurie.miaosha.service.PromoService;
import com.yurie.miaosha.service.model.ItemModel;
import com.yurie.miaosha.service.model.PromoModel;
import com.yurie.miaosha.validator.ValidationImpl;
import com.yurie.miaosha.validator.ValidationResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Autowired
    private StockLogDOMapper stockLogDOMapper;

    @Autowired
    private PromoService promoService;

    @Autowired
    private ValidationImpl validator;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MqProducer mqProducer;

    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        // 入参校验
        ValidationResult result = validator.validate(itemModel);
        if (result.isHasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, result.getErrMsg());
        }
        // 将model转化为DO
        ItemDO itemDO = convertItemDOFromItemModel(itemModel);

        // 写入数据库
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());
        ItemStockDO itemStockDO = convertItemStockDOFromItemModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);
        // 返回创建完成的对象，让上游知道创建完成的对象是什么样子的
        return getItemById(itemModel.getId());
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if (itemDO == null) {
            return null;
        }
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
        ItemModel itemModel = covertItemModelFromItemDO(itemDO, itemStockDO);
        // 获取秒杀活动
        PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());
        if (promoModel != null && promoModel.getStatus() != 3) {
            itemModel.setPromoModel(promoModel);
        }
        return itemModel;
    }

    @Override
    public ItemModel getItemByIdInCache(Integer id) {
        ItemModel itemModel = (ItemModel) redisTemplate.opsForValue().get("item_validate_" + id);
        if (itemModel == null) {
            itemModel = this.getItemById(id);
            redisTemplate.opsForValue().set("item_validate_" + id, itemModel);
            redisTemplate.expire("item_validate_" + id, 10, TimeUnit.MINUTES);
        }
        return itemModel;
    }

    @Override
    public List<ItemModel> listItem() {
        List<ItemDO> itemDOList = itemDOMapper.listItem();
        List<ItemModel> itemModelList = itemDOList.stream().map(itemDO -> {
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
            ItemModel itemModel = covertItemModelFromItemDO(itemDO, itemStockDO);
            return itemModel;
        }).collect(Collectors.toList());
        return itemModelList;
    }

    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {
        // 这里的sql语句传回来的是所影响的行数，根据是否为0可以判断是否成功。
        // 而采用“for update的sql语句先锁住库存并和amount进行比较，看下是否够扣，如果够再进行更新”的方案，
        // 需要两条sql语句，效率会低一点。
//        int affectedRows = itemStockDOMapper.decreaseStock(itemId, amount);
//        if (affectedRows > 0) {
//            // 扣减库存成功
//            return true;
//        } else {
//            // 扣减库存失败
//            return false;
//        }
        Long result = redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue() * (-1));
        if (result > 0) {
            // 扣减库存成功，发送消息。
            return true;
        } else if (result == 0) {
            // 设置库存售罄标识
            redisTemplate.opsForValue().set("promo_item_stock_invalid" + itemId, true);
            // 扣减库存成功，发送消息。
            return true;
        } else {
            // 扣减库存失败
            increaseSales(itemId, amount);
            return false;
        }
    }

    @Override
    public boolean asyncDecreaseStock(Integer itemId, Integer amount) {
        boolean mqResult = mqProducer.asyncReduceStock(itemId, amount);
        return mqResult;
    }

    @Override
    public boolean increaseStock(Integer itemId, Integer amount) {
        redisTemplate.opsForValue().increment("promo_item_stock_" + itemId, amount.intValue());
        // 这里不考虑redis操作失败的情况。
        return true;
    }

    @Override
    @Transactional
    public String initStockLog(Integer itemId, Integer amount) {
        StockLogDO stockLogDO = new StockLogDO();
        stockLogDO.setItemId(itemId);
        stockLogDO.setAmount(amount);
        stockLogDO.setStockLogId(UUID.randomUUID().toString().replace("-", ""));
        stockLogDO.setStatus(1);

        stockLogDOMapper.insert(stockLogDO);

        return stockLogDO.getStockLogId();
    }

    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) throws BusinessException {
        itemDOMapper.increaseSales(itemId, amount);
    }

    // itemModel->itemDO
    private ItemDO convertItemDOFromItemModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel, itemDO);
        itemDO.setPrice(itemModel.getPrice().doubleValue());
        return itemDO;
    }

    // itemModel->itemStockDO
    private ItemStockDO convertItemStockDOFromItemModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemStockDO itemStockDO = new ItemStockDO();
        itemStockDO.setStock(itemModel.getStock());
        itemStockDO.setItemId(itemModel.getId());
        return itemStockDO;
    }

    // itemDO和itemStockDO->itemModel
    private ItemModel covertItemModelFromItemDO(ItemDO itemDO, ItemStockDO itemStockDO) {
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO, itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;
    }
}
