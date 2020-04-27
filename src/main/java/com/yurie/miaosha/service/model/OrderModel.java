package com.yurie.miaosha.service.model;

import java.math.BigDecimal;

// 订单模型
public class OrderModel {
    // 订单编号，一般订单会有明确含义，所以这里采用String类型，而不用自增id。
    private String id;
    // 下单用户id
    private Integer userId;
    // 购买商品id
    private Integer itemId;
    // 购买商品数量
    private Integer amount;
    // 订单总价
    private BigDecimal orderPrice;
    // 下单时的商品单价（由于商品价格是会变动的，因此需要记录）
    private BigDecimal itemPrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(BigDecimal orderPrice) {
        this.orderPrice = orderPrice;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }
}
