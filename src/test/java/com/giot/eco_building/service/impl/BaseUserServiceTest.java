package com.giot.eco_building.service.impl;

import com.giot.eco_building.EcoBuildingApplication;
import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.entity.User;
import com.giot.eco_building.exception.TestIOException;
import com.giot.eco_building.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @Author: pyt
 * @Date: 2020/6/15 17:18
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EcoBuildingApplication.class)
@EnableAutoConfiguration
public class BaseUserServiceTest {
    @Autowired
    private UserService userService;

    @Test
    public void insert() throws IOException, TestIOException {
        User user = new User();
        user.setUsername("test");
        user.setPassword("123456");
        WebResponse response = userService.insert(user);
        System.out.println(response);
    }
}