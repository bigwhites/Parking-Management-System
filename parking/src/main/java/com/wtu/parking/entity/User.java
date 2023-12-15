package com.wtu.parking.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {
    Integer uId;
    String nickName;
    String teleNum;
    int uLevel;
    int balance;
    String pwd;
    String uSex;

    //连表数据
    List<Record> records;
    Date startTime; //月卡的开始时间  为null代表无月卡
    Integer effective;  //月卡有效期

    int credit;

    public boolean isMonthlyUser() {
        if (effective == null || startTime == null) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, effective);
        Date calendarTime = calendar.getTime();
        return calendarTime.after(new Date(System.currentTimeMillis()));
    }
}
