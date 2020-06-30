package com.giot.eco_building.service;

import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.constant.Constants;
import com.giot.eco_building.entity.User;

/**
 * @Author: pyt
 * @Date: 2020/6/15 14:46
 * @Description:
 */
public interface ActionService {
    void add(Constants.ActionType type, String msg);

    void add(Constants.ActionType type, String msg, User user);

    WebResponse getActionPage(Long userId,Integer number, Integer size, Integer actionType, String start, String end);

    WebResponse getActionType();
}
