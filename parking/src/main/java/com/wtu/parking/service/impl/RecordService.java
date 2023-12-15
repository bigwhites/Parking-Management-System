package com.wtu.parking.service.impl;

import com.wtu.parking.entity.User;
import com.wtu.parking.mapper.RecordMapper;
import com.wtu.parking.mapper.SystemConfigMapper;
import com.wtu.parking.mapper.UserMapper;
import com.wtu.parking.service.IRecordService;
import com.wtu.parking.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.wtu.parking.entity.Record;

import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.List;

@Service
@Slf4j
public class RecordService implements IRecordService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    RecordMapper recordMapper;

    @Autowired
    SystemConfigMapper systemConfigMapper;

    @Override
    public List<Record> getByUId(Integer uId) {
        if (userMapper.userExist(uId) != 1) {
            return null;
        }
        return recordMapper.getByUId(uId);
    }

    @Override
    public Record leave(Integer uId, Integer leaveGId) {
        Record lastRecord = recordMapper.getUsrLastRecord(uId);
        User user = userMapper.findByUId(uId);
        if (user == null) {
            throw new RuntimeException("无该用户");
        }
        if (lastRecord.getPrice() != -1) {
            throw new RuntimeException("车辆未进场");
        }
        lastRecord.setLeaveGId(leaveGId);
        lastRecord.setLeaveTime(new Timestamp(System.currentTimeMillis()));
        if (user.isMonthlyUser()) { //月卡用户无需付费
            log.info("月卡用户直接放行");
            lastRecord.setPrice(0);
        } else {  //常规用户需要收费
            Timestamp enterTime = lastRecord.getEnterTime();
            int hourDiff = TimeUtils.hourBetwnTimeSTamp(enterTime, lastRecord.getLeaveTime());
            log.info("hour diff = {}", hourDiff);
            Integer pricePerH = systemConfigMapper.getPricePerH();
            int price = Math.min(hourDiff * pricePerH, systemConfigMapper.getMaxPricePerD());
            lastRecord.setPrice(price);
            log.info(lastRecord.toString());
            if (user.getBalance() < price) {
                throw new RuntimeException("余额不足");
            }
            user.setBalance(user.getBalance() - price);
            userMapper.upDate(user);
        }
        recordMapper.upDateLastRec(lastRecord);
        systemConfigMapper.setUsedCount(systemConfigMapper.getUsedCount() - 1);
        return lastRecord;
    }

    @Override
    public int enter(Record record) {
        log.info("debug");
        record.setCarNum(record.getCarNum().replaceAll("\\s*",""));
        Record usrLastRecord = recordMapper.getUsrLastRecord(record.getUId());
        if (usrLastRecord != null && usrLastRecord.getPrice() == -1) {  //还未离场
            log.info("车辆未离场");
            throw new RuntimeException("车辆未离场");
        } else {  //写入数据库
            int numCntInParking = recordMapper.carNumCntInParking(record.getCarNum());
            if (numCntInParking > 0) {
                throw new RuntimeException("该车辆已经在场");
            }
            int usedCount = systemConfigMapper.getUsedCount();
            log.info("used ={}", usedCount);
            if (systemConfigMapper.getTotalPosition() <= usedCount) {
                throw new RuntimeException("剩余车位不足");
            }
            systemConfigMapper.setUsedCount(usedCount + 1);
            log.info("允许车辆进入");
            return recordMapper.insertNewRec(record);
        }
    }
}
