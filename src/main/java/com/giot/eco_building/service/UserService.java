package com.giot.eco_building.service;

import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.entity.User;
import com.giot.eco_building.exception.TestIOException;

import java.io.IOException;

/**
 * @Author: pyt
 * @Date: 2020/6/9 10:17
 * @Description:
 */
public interface UserService {
    /**
     * 添加新用户
     *
     * @return
     */
    WebResponse insert(User user);

    /**
     * 根据userId删除用户
     *
     * @param userId
     * @return
     */
    WebResponse delete(Long userId);

    /**
     * 获取用户分页
     * @param number
     * @param size
     * @return
     */
    WebResponse getUserPage(Integer number,Integer size);

    /**
     * 查询用户信息
     *
     * @param username
     * @return
     */
    User findByUsername(String username);
}
