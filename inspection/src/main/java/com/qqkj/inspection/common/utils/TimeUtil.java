package com.qqkj.inspection.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtil {
    public static void main(String[] args) {
        int i=-30;
        int j=40;
        double c=70.0/56.0;
        for (int a=0;a<56;a++){
            double b=-30+a*c;
            System.out.print(b+",");
        }
    }

    public static LocalDateTime getTime(String time){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime lt = LocalDateTime.parse(time, dateTimeFormatter);
        return lt;
    }


}
