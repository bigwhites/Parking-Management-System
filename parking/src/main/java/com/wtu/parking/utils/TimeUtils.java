package com.wtu.parking.utils;

import java.sql.Timestamp;
import java.util.Date;

public class TimeUtils {
    public static int hourBetwnTimeSTamp(Timestamp tPre, Timestamp t) {
        long tPreTime = tPre.getTime();
        long time = t.getTime();
        long diff = time - tPreTime;
        double diffHour = (double) diff / (1000 * 60 * 60);
        return (diffHour + 0.5 > (int) diffHour + 1)  //四舍五入超过半小时算1h
                ? (int) diffHour + 1 : (int) diffHour;
    }

}
