package com.wtu.parking.mapperTest;

import com.wtu.parking.entity.Manager;
import com.wtu.parking.mapper.ManagerMapper;
import com.wtu.parking.service.impl.MangerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ManagerTest {
    @Autowired
    ManagerMapper managerMapper;
    @Autowired
    MangerService mangerService;

    @Test
    void findById(){
        System.out.println(managerMapper.findByMId(20001));
    }

    @Test
    void insert(){
        System.out.println(managerMapper.insert(new Manager(null,"陈宇凡","女",
                "4440301200411115656",20000,1,"111")));
    }

    @Test
    void sinsert(){
        System.out.println(mangerService.insert(new Manager(null,"陈宇凡","女",
                "4440301200411115656",20000,1,"111")));
    }

    @Test
    void re() {
        String s = "ss   ss";
        System.out.println(s.replace(" ",""));
    }
}
