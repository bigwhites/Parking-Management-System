package com.wtu.parking.service;

import com.wtu.parking.entity.RechargeHistory;
import com.wtu.parking.entity.User;
import com.wtu.parking.utils.Page;

import java.util.List;
import java.util.Map;

public interface IUserService {
    Integer register(User user);

    User findByTeleNum(String teleNum);

    User findByUid(int uId);

    Integer charge(int amount,int uId);

    List<RechargeHistory> getAllHisByUser(int uId);


    User renewMonthlycard(int uId,int effective);

    int conversionGoods(int uId,int gId,String address);

    Page<User> selectByPage(int pageSize,int pageNum);

    Map<String,Integer> getSysConfig();

    User upDateUser(User user);
}
