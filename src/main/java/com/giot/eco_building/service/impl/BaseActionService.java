package com.giot.eco_building.service.impl;

import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.constant.Constants;
import com.giot.eco_building.entity.Action;
import com.giot.eco_building.entity.User;
import com.giot.eco_building.repository.ActionRepository;
import com.giot.eco_building.service.ActionService;
import com.giot.eco_building.utils.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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
        User user = IpUtil.getUser();
        add(type, msg, user);
    }

    @Override
    public void add(Constants.ActionType type, String msg, User user) {
        if (user != null) {
            Action action = new Action();
            action.setUserId(user.getId());
            action.setUserName(user.getUsername());
            action.setActionIp(IpUtil.getIpAddr());
            action.setType(type.getCode());
            action.setActionDesc(type.getValue() + msg);
            action.setActionTime(new Date());
            actionRepository.save(action);
        }
    }

    /**
     * 管理员查看全部操作日志
     *
     * @param number
     * @param size
     * @return
     */
    @Override
    public WebResponse getActionPage(Long userId, Integer number, Integer size, Integer actionType, String start, String end) {
        Sort sort = Sort.by(Sort.Direction.DESC,
                "actionTime"); //创建时间降序排序
        Pageable pageable = PageRequest.of(number - 1, size, sort);
        /*if (actionType != -1) {
            Integer[] types = {actionType};
            if (flag) {
                actionPage = actionRepository.findAllByTypeInAndActionTimeBetween(types, sdate, edate, pageable);
            } else {
                actionPage = actionRepository.findAllByTypeIn(types, pageable);
            }
        } else {
            Integer[] types = {0, 1, 2, 3};
            if (flag) {
                actionPage = actionRepository.findAllByTypeInAndActionTimeBetween(types, sdate, edate, pageable);
            } else {
                actionPage = actionRepository.findAllByTypeIn(types, pageable);
            }
        }*/
        Specification<Action> actionSpecification = (Specification<Action>) (root, criteriaQuery, criteriaBuilder) -> {
            //处理时间数据
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date sdate = new Date();
            Date edate = new Date();
            boolean flag = false;
            if (start != null && end != null) {
                flag = true;
                try {
                    sdate = sdf.parse(start);
                    edate = sdf.parse(end);
                    Calendar c = Calendar.getInstance();
                    c.setTime(edate);
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    edate = c.getTime();
                } catch (ParseException e) {
                    flag = false;
                }
            }
            List<Predicate> list = new ArrayList<>();//查询条件集
            //1.userId
            if (userId != null) list.add(criteriaBuilder.equal(root.get("userId").as(Long.class), userId));
            //2.type
            Path<Integer> path = root.get("type");
            CriteriaBuilder.In<Integer> in = criteriaBuilder.in(path);
            if (actionType != -1) {
                in.value(actionType);
            } else {
                in.value(0);
                in.value(1);
                in.value(2);
                in.value(3);
                in.value(4);
            }
            list.add(criteriaBuilder.and(criteriaBuilder.and(in)));
            //3.date
            if (flag) list.add(criteriaBuilder.between(root.get("actionTime"), sdate, edate));
            return criteriaBuilder.and(list.toArray(new Predicate[list.size()]));
        };
        Page<Action> actionPage = actionRepository.findAll(actionSpecification, pageable);

        return WebResponse.success(actionPage.getContent(), actionPage.getTotalPages(), actionPage.getTotalElements());
    }

    /**
     * 操作类型：
     * 登入/登出-0
     * 上传-3
     * 删除-2
     * 全部--1
     *
     * @return
     */
    @Override
    public WebResponse getActionType() {
        Object[] list = {
                "登入/登出", 0,
                "项目修改", 4,
                "上传", 3,
                "全部", -1};
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 0; i < list.length / 2; i++) {
            int s = i * 2;
            int e = i * 2 + 1;
            String lable = (String) list[s];
            Integer value = (Integer) list[e];
            Map<String, Object> map = new HashMap<>();
            map.put("label", lable);
            map.put("value", value);
            result.add(map);
        }
        return WebResponse.success(result);
    }
}
