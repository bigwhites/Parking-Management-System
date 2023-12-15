package com.wtu.parking.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsTrade implements Serializable {
    Integer trId;
    int uId;
    int gId;
    String address;
    int status;
    String logisticsNum; //运单号

    int price;

}
