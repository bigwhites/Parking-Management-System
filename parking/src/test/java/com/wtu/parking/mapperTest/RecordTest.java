package com.wtu.parking.mapperTest;

import com.wtu.parking.entity.Record;
import com.wtu.parking.mapper.RecordMapper;
import com.wtu.parking.mapper.SystemConfigMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class RecordTest {

    @Autowired
    RecordMapper recordMapper;

    @Autowired
    SystemConfigMapper systemConfigMapper;

    @Test
    void findByid(){
        List<Record> byUId = recordMapper.getByUId(202301);
        for (Record r : byUId){
            System.out.println(r.toString());
        }
    }

    @Test
    void last(){
        System.out.println(recordMapper.getUsrLastRecord(202301).toString());
        System.out.println(systemConfigMapper.getPricePerH());
    }

    @Test
    void insertNew(){
        Record record = new Record();
        record.setUId(202301);
        record.setCarNum("鄂A G7Q21");
        record.setEnterGId(1);
        record.setCarPosition(null);
        System.out.println(record.toString());
        recordMapper.insertNewRec(record);
    }

}
