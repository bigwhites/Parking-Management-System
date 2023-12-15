package com.wtu.parking.entity;


import lombok.Data;

import java.io.Serializable;

@Data
public class Goods implements Serializable {
    Integer gId;
    String gName;
    String gInfo;
    int needCredit;

    int count;
}
