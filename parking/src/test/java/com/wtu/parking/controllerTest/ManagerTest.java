package com.wtu.parking.controllerTest;

import com.wtu.parking.controller.ManagerController;
import com.wtu.parking.service.impl.MangerService;
import com.wtu.parking.service.impl.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ManagerTest {

    @Autowired
    ManagerController managerController;
    @Autowired
    UserService userService;

    @Test
    void log() {
        System.out.println(managerController.login("20001", null, "111"));
    }

    @Test
    void getHis() {
        System.out.println(userService.getAllHisByUser(202301).get(0).toString());
    }
}
