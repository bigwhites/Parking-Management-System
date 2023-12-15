package com.wtu.parking.service.impl;

import com.wtu.parking.entity.Goods;
import com.wtu.parking.entity.GoodsTrade;
import com.wtu.parking.entity.RechargeHistory;
import com.wtu.parking.entity.User;
import com.wtu.parking.mapper.GoodsMapper;
import com.wtu.parking.mapper.GoodsTradeMapper;
import com.wtu.parking.mapper.SystemConfigMapper;
import com.wtu.parking.mapper.UserMapper;
import com.wtu.parking.service.IUserService;
import com.wtu.parking.utils.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class UserService implements IUserService {
    @Autowired
    UserMapper userMapper;
    @Autowired
    SystemConfigMapper systemConfigMapper;
    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    GoodsTradeMapper goodsTradeMapper;

    @Override
    public Integer register(User user) {
        try {
            user.setTeleNum(user.getTeleNum().replace(" ", ""));
            final String regex = "^1[3456789]\\d{9}$";
            if (!user.getTeleNum().matches(regex)) {
                return -2;
            }
            int r = userMapper.insert(user);
            if (r == 0) {
                return -1;
            } else return userMapper.getLastId();
        } catch (Exception e) {
            log.error(e.getMessage());
            return -1;
        }
    }

    @Override
    public User findByTeleNum(String teleNum) {
        //TODO 手机号码的正则校验
        return userMapper.findByTeleNum(teleNum);
    }

    @Override
    public User findByUid(int uId) {
        return userMapper.findByUId(uId);
    }

    @Override
    public Integer charge(int amount, int uId) {
        User user = userMapper.findByUId(uId);
        if (user == null) {
            return -1;  //no user
        }
        Integer sumRecharge = userMapper.sumRecharge(uId);
        int x = 0;
        if (sumRecharge == null) {
            sumRecharge = amount;
        } else {
            sumRecharge += amount;
        }
        if (sumRecharge > 5000) {
            x = 4;
        } else if (sumRecharge > 2500) {
            x = 3;
        } else if (sumRecharge > 200) {
            x = 2;
        } else {
            x = 1;
        }
        user.setCredit(user.getCredit() + amount);
        user.setULevel(x);
        user.setBalance(user.getBalance() + amount);
        int i = userMapper.upDate(user);
        int res = userMapper.insertChargeRecord(
                new RechargeHistory(null, uId, amount, null));
        return amount;
    }

    @Override
    public List<RechargeHistory> getAllHisByUser(int uId) {
        try {
            return userMapper.getHistoriesByUser(uId);
        } catch (Exception e) {
            return null;
        }
    }


    @Override
    public User renewMonthlycard(int uId, int effective) {
        if (effective < 1) {
            throw new RuntimeException("月数必须大于1");
        }
        User user = userMapper.findByUId(uId);
        Date startTime = user.getStartTime();
        Integer orignEffective = user.getEffective();
        if (startTime == null) {
            throw new RuntimeException("还不是月卡不能付费");
        }
        int price = systemConfigMapper.getMonthlyPrice() * effective;
        if (user.getBalance() < price) {
            throw new RuntimeException("用户余额不足");
        }
        Calendar endDateC = Calendar.getInstance();
        endDateC.setTime(startTime);
        endDateC.add(Calendar.DAY_OF_YEAR, orignEffective);
        Date endDate = endDateC.getTime();

        if (endDate.after(new Date(System.currentTimeMillis()))) {     // 未过期
            userMapper.updateMonthlyUser(uId, orignEffective + effective * 30, user.getStartTime());
        } else {
            userMapper.updateMonthlyUser(uId, effective * 30, new Date(System.currentTimeMillis()));
        }
        user.setBalance(user.getBalance() - price);
        userMapper.upDate(user);
        return userMapper.findByUId(uId);
    }

    @Override
    public int conversionGoods(int uId, int gId, String address) {
        if (address == null || address.equals("")) {
            throw new RuntimeException("地址不能为空");
        }
        User user = userMapper.findByUId(uId);
        if (user == null) {
            throw new RuntimeException("无该用户");
        }
        Goods goods = goodsMapper.findBygId(gId);
        if (goods == null) {
            throw new RuntimeException("无该商品");
        }
        if (goods.getNeedCredit() > user.getCredit()) {
            throw new RuntimeException("用户积分不足");
        }
        if (goods.getCount() < 1) {
            throw new RuntimeException("商品余额不足");
        }
        user.setCredit(user.getCredit() - goods.getNeedCredit());
        goods.setCount(goods.getCount() - 1);
        userMapper.upDate(user);
        goodsMapper.update(goods);
        goodsTradeMapper.insertTrRec(
                new GoodsTrade(null, uId, gId, address, 0, null, goods.getNeedCredit()));
        return 1;
    }

    @Override
    public Page<User> selectByPage(int pageSize, int pageNum) {
        List<User> userList = userMapper.selectByPage(pageSize, pageNum * pageSize);
        Page<User> page = null;
        if (userList != null && userList.size() > 0) {
            page = new Page<>();
            page.setCurPage(pageNum);
            page.setPageSize(pageSize);
            page.setPage(userList);
            page.setTotal(userMapper.getUserCount());
            return page;
        } else {
            return null;
        }
    }

    @Override
    public Map<String, Integer> getSysConfig() {
        Map<String, Integer> map = new HashMap<>();
        map.put("usedCount", systemConfigMapper.getUsedCount());
        map.put("totalPosition", systemConfigMapper.getTotalPosition());
        map.put("monthlyPrice", systemConfigMapper.getMonthlyPrice());
        map.put("pricePerH", systemConfigMapper.getPricePerH());
        map.put("maxPricePerD", systemConfigMapper.getMaxPricePerD());
        return map;
    }

    @Override
    public User upDateUser(User user) {
        if (user == null) {
            throw new RuntimeException("user为空");
        }
        User byUId = userMapper.findByUId(user.getUId());
        if (byUId == null) {
            throw new RuntimeException("无该用户");
        }
        User byTeleNum = userMapper.findByTeleNum(user.getTeleNum());
        if (byTeleNum != null && !Objects.equals(byTeleNum.getUId(), user.getUId())) {
            throw new RuntimeException("该手机号已被使用");
        }
        if (user.getCredit() != byUId.getCredit() || user.getBalance() != byUId.getBalance()) {
            if (user.getULevel() != byUId.getULevel()) {
                userMapper.upDate(user);
            }
        }
        return user;
    }


}
