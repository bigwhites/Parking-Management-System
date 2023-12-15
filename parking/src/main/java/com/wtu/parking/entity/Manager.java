package com.wtu.parking.entity;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Manager implements Serializable {
    Integer mId;
    String mName;
    String mSex;
    String mIdentityId;
    Integer mSalary;
    Integer mLevel;  //0最高有所有权限  12无员工管理权限
    String pwd;
}
