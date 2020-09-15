package com.giot.eco_building.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: pyt
 * @Date: 2020/7/21 16:02
 * @Description:
 */
@RestController
public class LogoutController {
    @GetMapping("/logout")
    public void logout() {
        System.out.println("logout");
    }
}
