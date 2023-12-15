package com.wtu.parking.mapper;

import com.wtu.parking.entity.GoodsTrade;

import java.util.List;

public interface GoodsTradeMapper {
    int insertTrRec(GoodsTrade goodsTrade);

    int update(GoodsTrade goodsTrade);

    List<GoodsTrade> getByUId(int uId);
    GoodsTrade getByTrId(int trId);

    List<GoodsTrade> getByTrRecordPage(int limit ,int offset);

    int getByTrRecordCnt();
}
