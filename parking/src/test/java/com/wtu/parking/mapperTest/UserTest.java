package com.wtu.parking.mapperTest;

import com.wtu.parking.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserTest {
    @Autowired
    UserMapper userMapper;

    @Test
    void ftnum(){
        System.out.println(userMapper.findByUId(202301));
    }

    @Test
    void maxx(){
        System.out.println(userMapper.getLastId());
    }

    @Test
    void exist(){
        System.out.println(userMapper.userExist(202301));
    }
}
