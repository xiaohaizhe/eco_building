package com.giot.eco_building.controller;

import com.giot.eco_building.aop.SystemControllerLog;
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

    @PostMapping("/register")
    @SystemControllerLog(description = "添加用户")
    public WebResponse save(@RequestBody User user) {
        return userService.insert(user);
    }

    @PostMapping("/update")
    @SystemControllerLog(description = "编辑用户")
    public WebResponse update(@RequestBody User user) {
        return userService.insert(user);
    }

    @PostMapping("/delete")
    @SystemControllerLog(description = "删除用户")
    public WebResponse delete(Long userId) {
        return userService.delete(userId);
    }

    @GetMapping("/userPage")
    public WebResponse userPage(Integer number, Integer size) {
        return userService.getUserPage(number, size);
    }

}
