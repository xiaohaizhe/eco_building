package com.giot.eco_building.config;

import com.alibaba.fastjson.JSON;
import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.constant.Constants;
import com.giot.eco_building.service.ActionService;
import com.giot.eco_building.utils.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 认证成功处理器
 */
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private ActionService actionService;

    @Autowired
    public CustomAuthenticationSuccessHandler(ActionService actionService) {
        this.actionService = actionService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        actionService.add(Constants.ActionType.LOGIN, "成功");
        Map<String, Object> user = new HashMap<>();
        user.put("id", IpUtil.getUser().getId());
        user.put("authority", IpUtil.getUser().getAuthority());
        WebResponse response = WebResponse.success(user);
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        httpServletResponse.getWriter().write(JSON.toJSONString(response));
    }

}