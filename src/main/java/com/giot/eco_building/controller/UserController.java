package com.giot.eco_building.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: pyt
 * @Date: 2020/6/9 11:03
 * @Description:
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @GetMapping("/testUser")
    public String testUser(){
        return "I am user. I can get this interface.";
    }
}
