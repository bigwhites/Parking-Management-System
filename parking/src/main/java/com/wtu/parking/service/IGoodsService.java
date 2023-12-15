package com.wtu.parking.service;

import com.wtu.parking.entity.Goods;
import com.wtu.parking.entity.GoodsTrade;
import com.wtu.parking.utils.Page;

import java.util.List;

public interface IGoodsService {
    Goods findByGid(int gId);
    int insert(Goods good);
    int update(Goods good);
    int deleteById(int gId);

    Page<Goods> getPage(int pageSize,int pageNum);

    int updateTrade(GoodsTrade goodsTrade);

    List<GoodsTrade> getAllTrByUId(int uId);

    GoodsTrade getByTrId(int trId);

    int acceptGood(Integer trId);

    Page<GoodsTrade> GoodTrRecordByPage(int pageSize, int pageNum);
}
