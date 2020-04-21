package com.yurie.miaosha.response;

public class CommonReturnType {
    // 表示请求成功或失败的状态，success或fail
    private String status;
    // 若status=success，data返回前端需要的json数据；
    // 若status=fail，data返回通用的错误码格式。
    private Object data;

    public static CommonReturnType create(Object data) {
        return create("success", data);
    }

    public static CommonReturnType create(String status, Object data) {
        CommonReturnType commonReturnType = new CommonReturnType();
        commonReturnType.setStatus(status);
        commonReturnType.setData(data);
        return commonReturnType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
