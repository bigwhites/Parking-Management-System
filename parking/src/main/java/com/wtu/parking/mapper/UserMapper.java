package com.wtu.parking.mapper;

import com.wtu.parking.entity.RechargeHistory;
import com.wtu.parking.entity.User;
import io.swagger.models.auth.In;

import java.util.Date;
import java.util.List;

public interface UserMapper {
    int insert(User user);

    User findByTeleNum(String teleNum);

    User findByUId(int uId);

    Integer getLastId();

    Integer userExist(Integer uId);

    int charge(int amount, int uId);

    int insertChargeRecord(RechargeHistory history);

    List<RechargeHistory> getHistoriesByUser(int uId);

    int upDate(User user);

    int insertMonthlyCard(int uId, int effective);

    int updateMonthlyUser(int uId, int effective, Date date);

    Integer sumRecharge(int uId);

    List<User> selectByPage(int limit,int offset);

    int getUserCount();

}
