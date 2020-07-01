package com.giot.eco_building.controller;

import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.service.ActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: pyt
 * @Date: 2020/6/16 14:33
 * @Description:
 */
@RestController
@RequestMapping("/actions")
public class ActionController {
    private ActionService actionService;

    @Autowired
    public void setActionService(ActionService actionService) {
        this.actionService = actionService;
    }

    @GetMapping("/actionType")
    public WebResponse getActionType() {
        return actionService.getActionType();
    }

    @GetMapping("/actionPage")
    public WebResponse getActionPages(Integer number, Integer size, Integer actionType, String start, String end) {
        return actionService.getActionPage(null, number, size, actionType, start, end);
    }

    @GetMapping("/actionPageByUserId")
    public WebResponse actionPageByUserId(Long userId, Integer number, Integer size, Integer actionType, String start, String end) {
        return actionService.getActionPage(userId, number, size, actionType, start, end);
    }
}
