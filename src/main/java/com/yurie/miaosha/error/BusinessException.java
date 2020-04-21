package com.yurie.miaosha.error;

//包装器业务一场类实现
public class BusinessException extends Exception implements CommonError {

    private CommonError commonError;

    // 直接接收EmBusinessError的传参构造业务异常
    public BusinessException(CommonError commonError) {
        // 调用super()是因为Exception本身需要一些初始化
        super();
        this.commonError = commonError;
    }

    // 接收自定义的errMsg构造业务异常
    public BusinessException(CommonError commonError, String errMsg) {
        super();
        this.commonError = commonError;
        this.commonError.setErrMsg(errMsg);
    }

    @Override
    public int getErrCode() {
        return this.commonError.getErrCode();
    }

    @Override
    public String getErrMsg() {
        return this.commonError.getErrMsg();
    }

    @Override
    public CommonError setErrMsg(String errMsg) {
        this.commonError.setErrMsg(errMsg);
        return this;
    }
}
