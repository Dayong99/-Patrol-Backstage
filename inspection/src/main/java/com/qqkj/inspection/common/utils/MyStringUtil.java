package com.qqkj.inspection.common.utils;

import org.apache.commons.lang3.StringUtils;

public class MyStringUtil
{
    public static String trimBothEndsChars(String srcStr, String splitter)
    {
        String regex = "^" + splitter + "*|" + splitter + "*$";
        return srcStr.replaceAll(regex, "");
    }

    public static String parseTransProcessStr(String str1,String str2,String str3)
    {
        String targetStr = "";

        str1 = str1.trim();
        str2 = str2.trim();
        str3 = str3.trim();
        if(StringUtils.isNotBlank(str1))
        {
            str1 += ",";
        }
        if(StringUtils.isNotBlank(str2))
        {
            str2 += ",";
        }
        targetStr = str1 + str2 + str3;
        System.out.println(targetStr);
        return trimBothEndsChars(targetStr,",");
    }
}
