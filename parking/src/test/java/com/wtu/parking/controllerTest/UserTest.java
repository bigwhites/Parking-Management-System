package com.wtu.parking.controllerTest;

import com.wtu.parking.controller.UserController;
import com.wtu.parking.entity.User;
import com.wtu.parking.mapper.UserMapper;
import com.wtu.parking.utils.ApiResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserTest {
    @Autowired
    UserController userController;
    @Autowired
    UserMapper userMapper;

    @Test
    void leave(){
        ApiResult leave = userController.leave(202301,1);
        System.out.println(leave.getData().toString());

    }

    @Test
    void charge(){
        ApiResult recharge = userController.recharge(1, 202305);
        System.out.println(recharge.getData().toString());
    }

    @Test
    void update(){
        User uId = userMapper.findByUId(202301);
        System.out.println(uId.toString());
        uId.setNickName("魏雄");
        System.out.println(userController.upadteUser(uId).toString());
    }

}
