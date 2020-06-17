package com.giot.eco_building.service;

import com.giot.eco_building.constant.Constants;

/**
 * @Author: pyt
 * @Date: 2020/6/15 14:46
 * @Description:
 */
public interface ActionService {
    void add(Constants.ActionType type, String msg);

    void add(Constants.ActionType type, String msg, Long userId);
}
