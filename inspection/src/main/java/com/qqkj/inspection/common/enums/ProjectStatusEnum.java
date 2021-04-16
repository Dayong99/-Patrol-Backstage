package com.qqkj.inspection.common.enums;

/**
 * 项目状态枚举
 *
 * @author earthchen
 * @date 2018/9/30
 **/
public enum ProjectStatusEnum
{

    OK("0", "正常"), DISABLE("1", "停用"), DELETED("1", "删除");

    private final String code;

    private final String info;

    ProjectStatusEnum(String code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public String getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return info;
    }
}
