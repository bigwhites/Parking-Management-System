package com.wtu.parking.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RechargeHistory implements Serializable {
    Integer cId;
    int uId;
    int amount;
    Date cDate;
}