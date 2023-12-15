package com.wtu.parking.service.impl;

import com.wtu.parking.entity.Manager;
import com.wtu.parking.entity.User;
import com.wtu.parking.mapper.ManagerMapper;
import com.wtu.parking.mapper.SystemConfigMapper;
import com.wtu.parking.mapper.UserMapper;
import com.wtu.parking.service.IManagerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MangerService implements IManagerService {
    @Autowired
    ManagerMapper managerMapper;
    @Autowired
    UserMapper userMapper;

    @Autowired
    SystemConfigMapper systemConfigMapper;

    @Override
    public Manager findByMId(Integer mId) {

        return managerMapper.findByMId(mId);
    }

    @Override
    public Manager findByIdentityId(String identityId) {
        return managerMapper.findByIdentityId(identityId);
    }

    @Override
    public int insert(Manager manager) {
        try {
            int x = managerMapper.insert(manager);
            if (x != 0) {
                return managerMapper.getLastId();
            }
            return -1;
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public int insertMonthlyUser(int uId, int effective) {
        User user = userMapper.findByUId(uId);
        Integer monthlyPrice = systemConfigMapper.getMonthlyPrice();
        int price = effective * monthlyPrice;
        if (user.getBalance() < price) {
            throw new RuntimeException("余额不足");
        }
        if (user.getStartTime() != null) {
            throw new RuntimeException("已经是月卡用户");
        }
        user.setBalance(user.getBalance() - price);
        userMapper.upDate(user);
        userMapper.insertMonthlyCard(uId, effective * 30);
        return price;
    }
}
