package com.giot.eco_building.controller;

import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.entity.User;
import com.giot.eco_building.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: pyt
 * @Date: 2020/6/9 11:04
 * @Description:
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/test")
    public String testUser(){
        return "I am admin. I can get this interface.";
    }

    @PostMapping("/register")
    public WebResponse save(@RequestBody User user){
        return userService.insert(user);
    }

}
