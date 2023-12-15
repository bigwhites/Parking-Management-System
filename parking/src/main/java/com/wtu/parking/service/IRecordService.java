package com.wtu.parking.service;

import com.wtu.parking.entity.Record;

import java.util.List;

public interface IRecordService {
    List<Record> getByUId(Integer uId);

    Record leave(Integer uId,Integer leaveGId);

    int enter(Record record);
}
