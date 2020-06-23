package com.giot.eco_building.service.impl;

import com.giot.eco_building.constant.Constants;
import com.giot.eco_building.entity.Action;
import com.giot.eco_building.repository.ActionRepository;
import com.giot.eco_building.service.ActionService;
import com.giot.eco_building.utils.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author: pyt
 * @Date: 2020/6/15 16:15
 * @Description:
 */
@Service
public class BaseActionService implements ActionService {
    private ActionRepository actionRepository;

    @Autowired
    public void setActionRepository(ActionRepository actionRepository) {
        this.actionRepository = actionRepository;
    }

    @Override
    public void add(Constants.ActionType type, String msg) {
        Long userId = IpUtil.getUserId();
        add(type, msg, userId);
    }

    @Override
    public void add(Constants.ActionType type, String msg, Long userId) {
        if (userId != null) {
            Action action = new Action();
            action.setUserId(userId);
            action.setActionIp(IpUtil.getIpAddr());
            action.setType(type.getCode());
            action.setActionDesc(type.getValue() + msg);
            action.setActionTime(new Date());
            actionRepository.save(action);
        }
    }
}
