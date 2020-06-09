package com.giot.eco_building.service.impl;

import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.constant.Constants;
import com.giot.eco_building.constant.HttpResponseStatusEnum;
import com.giot.eco_building.entity.User;
import com.giot.eco_building.repository.UserRepository;
import com.giot.eco_building.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @Author: pyt
 * @Date: 2020/6/9 10:19
 * @Description:
 */
@Service
@Primary
@Slf4j
public class BaseUserService implements UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public BaseUserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public WebResponse insert(User user) {
        String username = user.getUsername();
        if (exist(username)){
            return WebResponse.failure(HttpResponseStatusEnum.USER_HAS_EXISTED);
        }else {
            user.setPassword(encoder.encode(user.getPassword()));
            user.setDelStatus(Constants.DelStatus.NORMAL.isValue());
            user.setAuthority(Constants.Authority.USER.getValue());
            userRepository.save(user);
            return WebResponse.success();
        }
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    private boolean exist(String username){
        User user = userRepository.findByUsername(username);
        return (user!=null);
    }
}
