package com.wtu.parking.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Gate implements Serializable {
    int gateId;
    String gPosition;
    int onRun;
}
