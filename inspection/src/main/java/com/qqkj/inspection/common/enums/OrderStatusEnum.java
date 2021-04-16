package com.qqkj.inspection.common.enums;

public enum OrderStatusEnum
{


    UNRECEIVE(1, "未接单"), RECEIVE(2, "已接单"), FINISH(3, "已完成"), CANCEL(0, "已取消");

    private final Integer code;

    private final String info;

    OrderStatusEnum(Integer code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public Integer getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return info;
    }
}
