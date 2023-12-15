package com.wtu.parking.mapper;

import com.wtu.parking.entity.Record;

import java.util.List;

public interface RecordMapper {
    List<Record> getByUId(Integer uId);

    Record getUsrLastRecord(Integer uId);
    int upDateLastRec(Record record);
    int insertNewRec(Record record);

    int carNumCntInParking(String carNum);
}
