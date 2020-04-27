package com.yurie.miaosha.controller;

import com.yurie.miaosha.error.BusinessException;
import com.yurie.miaosha.error.EmBusinessError;
import com.yurie.miaosha.response.CommonReturnType;
import com.yurie.miaosha.service.OrderService;
import com.yurie.miaosha.service.model.OrderModel;
import com.yurie.miaosha.service.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller("order")
@RequestMapping("/order")
@CrossOrigin(origins = {"*"}, allowCredentials = "true")
public class OrderController extends BaseController {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private OrderService orderService;

    // 下单
    @RequestMapping(value = "/createorder", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType createOrder(@RequestParam(name = "itemId") Integer itemId,
                                        @RequestParam(name = "amount") Integer amount) throws BusinessException {
        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        System.out.println(httpServletRequest.getSession().getAttribute("IS_LOGIN"));
        if (isLogin == null || !isLogin.booleanValue()) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN,"用户未登陆，不能下单");
        }

        System.out.println(httpServletRequest.getSession().getAttribute("LOGIN_USER"));
        UserModel userModel = (UserModel) httpServletRequest.getSession().getAttribute("LOGIN_USER");

        System.out.println(userModel.getName());

        OrderModel orderModel = orderService.createOrder(userModel.getId(), itemId, amount);
        return CommonReturnType.create(null);
    }
}
