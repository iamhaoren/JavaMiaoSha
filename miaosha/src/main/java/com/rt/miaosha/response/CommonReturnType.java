package com.rt.miaosha.response;

public class CommonReturnType {

    //表明对应请求的处理结果的返回结果，“success”or“fail”
    private String status;

    //若status==success，则data返回前端需要的json数据
    //若status==fail，则data返回通用的错误码格式
    private Object data;

    public static CommonReturnType create(Object result){
        return CommonReturnType.create(result,"success");
    }

    public static CommonReturnType create(Object result,String status){
        CommonReturnType type=new CommonReturnType();
        type.setStatus(status);
        type.setData(result);
        return type;
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
