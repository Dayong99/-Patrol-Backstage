package com.qqkj.inspection.common.enums;

public enum OrderOilStatus
{
    QUALIFIED(1, "合格"), UNQUALIFIED(0, "不合格"), CONFIRM(1, "确认"), UNCONFIRM(0, "未确认");

    private final Integer code;
    private final String info;

    OrderOilStatus(Integer code, String info)
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
