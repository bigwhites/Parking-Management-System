package com.wtu.parking.entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Record implements Serializable {
    int recordId;
    int uId;
    String carNum;
    int  price;
    Timestamp enterTime;
    Timestamp leaveTime;
    int enterGId;
    Integer leaveGId;
    String carPosition;

    //连表数据
    User user;
    Gate enterGate;
    Gate leaveGate;

}
