package com.wxn.miaosha.response;

public class CommonReturnType {

    // 表示请求处理的结果状态
    private String status;

    // 如果status = success，则data内返回前端需要的json数据
    // 否则，data内使用通用的错误码格式
    private Object data;

    // 定义一个通用的创建方法
    public static CommonReturnType create(Object result){
        return create(result,"success");
    }

    public static CommonReturnType create(Object result,String status){
        CommonReturnType type = new CommonReturnType();
        type.setData(result);
        type.setStatus(status);
        return type;
    }


    public String getStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
