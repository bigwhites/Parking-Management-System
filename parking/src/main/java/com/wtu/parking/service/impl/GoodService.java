package com.wtu.parking.service.impl;

import com.wtu.parking.entity.Goods;
import com.wtu.parking.entity.GoodsTrade;
import com.wtu.parking.mapper.GoodsMapper;
import com.wtu.parking.mapper.GoodsTradeMapper;
import com.wtu.parking.service.IGoodsService;
import com.wtu.parking.utils.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class GoodService implements IGoodsService {
    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    GoodsTradeMapper goodsTradeMapper;

    @Override
    public Goods findByGid(int gId) {
        return goodsMapper.findBygId(gId);
    }

    @Override
    public int insert(Goods good) {
        return goodsMapper.insert(good);
    }

    @Override
    public int update(Goods good) {
        try {
            return goodsMapper.update(good);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int deleteById(int gId) {
        try {
            return goodsMapper.deleteById(gId);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public Page<Goods> getPage(int pageSize, int pageNum) {
        Page<Goods> page = new Page<>();
        List<Goods> byPage = goodsMapper.getByPage(pageSize, pageNum * pageSize);
        if (byPage == null || byPage.size() == 0) {
            return null;
        }
        page.setPage(byPage);
        page.setPageSize(pageSize);
        page.setCurPage(pageNum);
        page.setTotal(goodsMapper.getCount());
        return page;
    }

    @Override
    public int updateTrade(GoodsTrade goodsTrade) {
        return goodsTradeMapper.update(goodsTrade);
    }

    @Override
    public List<GoodsTrade> getAllTrByUId(int uId) {
        List<GoodsTrade> byUId = goodsTradeMapper.getByUId(uId);
        if (byUId == null || byUId.size() == 0) {
            return null;
        } else {
            return byUId;
        }
    }

    @Override
    public GoodsTrade getByTrId(int trId) {
        return goodsTradeMapper.getByTrId(trId);
    }

    @Override
    public int acceptGood(Integer trId) {
        GoodsTrade goodsTrade = goodsTradeMapper.getByTrId(trId);
        if (goodsTrade == null) {
            throw new RuntimeException("无改订单");
        } else if (goodsTrade.getStatus() != 1) {
            throw new RuntimeException("未发货或已经确认收货");
        } else {
            goodsTrade.setStatus(2);
            return goodsTradeMapper.update(goodsTrade);
        }
    }

    @Override
    public Page<GoodsTrade> GoodTrRecordByPage(int pageSize, int pageNum) {
        List<GoodsTrade> tradeList = goodsTradeMapper.getByTrRecordPage(pageSize, pageNum * pageSize);
        Page<GoodsTrade> page = null;
        if(tradeList!=null && tradeList.size()>0){
            page = new Page<>();
            page.setPage(tradeList);
            page.setCurPage(pageNum);
            page.setPageSize(pageSize);
            page.setTotal(goodsTradeMapper.getByTrRecordCnt());
            return page;
        }
        else {
            throw new RuntimeException("没有更多记录");
        }

    }
}
