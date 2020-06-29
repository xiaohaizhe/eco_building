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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

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
    public BaseUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 管理员注册用户/更新用户
     *
     * @param user
     * @return
     */
    @Override
    public WebResponse insert(User user) {
        String username = user.getUsername();
        if (username == null || "".equals(username)) {
            return WebResponse.failure(HttpResponseStatusEnum.USERNAME_NOT_EXISTED);
        }
        if (user.getId() == null) {
            if (exist(username)) {
                return WebResponse.failure(HttpResponseStatusEnum.USER_HAS_EXISTED);
            }
            String password = user.getPassword();
            password = encoder.encode(password);
            user.setPassword(password);
            user.setDelStatus(Constants.DelStatus.NORMAL.isValue());
            user.setAuthority(Constants.Authority.USER.getValue());
            user = userRepository.save(user);
            return WebResponse.success(user);
        } else {
            Optional<User> userOptional = userRepository.findById(user.getId());
            if (userOptional.isPresent()) {
                User userOld = userOptional.get();
                String password = user.getPassword();
                password = encoder.encode(password);
                userOld.setPassword(password);
                if (!username.equals(userOld.getUsername())) {
                    if (exist(username)) {
                        return WebResponse.failure(HttpResponseStatusEnum.USER_HAS_EXISTED);
                    } else {
                        userOld.setUsername(username);
                    }
                }
                userOld = userRepository.saveAndFlush(userOld);
                return WebResponse.success(userOld);
            } else {
                return WebResponse.failure(HttpResponseStatusEnum.USER_NOT_EXISTED);
            }
        }
    }

    @Override
    public WebResponse delete(Long userId) {
        Optional<User> optionalUser = userRepository.findByIdAndDelStatus(userId, Constants.DelStatus.NORMAL.isValue());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setDelStatus(Constants.DelStatus.DELETE.isValue());
            userRepository.saveAndFlush(user);
            return WebResponse.success();
        } else {
            return WebResponse.failure(HttpResponseStatusEnum.USER_NOT_EXISTED);
        }
    }

    @Override
    public WebResponse getUserPage(Integer number, Integer size) {
        Sort sort = Sort.by(Sort.Direction.DESC,
                "created"); //创建时间降序排序
        Pageable pageable = PageRequest.of(number, size, sort);
        Page<User> userPage = userRepository.findAllByAuthorityAndDelStatus(Constants.Authority.USER.getValue(),
                Constants.DelStatus.NORMAL.isValue(), pageable);
        return WebResponse.success(userPage.getContent(), userPage.getTotalPages(), userPage.getTotalElements());
    }

    @Override
    public User findByUsername(String username) {
        User user = userRepository.findByUsernameAndDelStatus(username, Constants.DelStatus.NORMAL.isValue());
        if (user != null) {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = requestAttributes.getRequest();
            HttpSession session = request.getSession();
            session.setAttribute("userId", user.getId());
        }
        return user;
    }

    /**
     * 检查用户名是否已存在
     * true-存在
     * false-不存在
     *
     * @param username
     * @return
     */
    private boolean exist(String username) {
        User user = userRepository.findByUsernameAndDelStatus(username, Constants.DelStatus.NORMAL.isValue());
        return (user != null);
    }
}
