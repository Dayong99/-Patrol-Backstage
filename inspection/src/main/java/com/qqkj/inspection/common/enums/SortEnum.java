package com.qqkj.inspection.common.enums;

public enum SortEnum
{
    ASC("ASC","ASC"),
    DESC("DESC","DESC"),
    CREATETIME("createTime","createTime"),
    ALERTTIME("alertTime","alertTime");
    private final String sortType;
    private final String sortValue;

    SortEnum(String sortType, String sortValue)
    {
        this.sortType = sortType;
        this.sortValue = sortValue;
    }

    public String getSortType()
    {
        return sortType;
    }

    public String getSortValue()
    {
        return sortValue;
    }
}
