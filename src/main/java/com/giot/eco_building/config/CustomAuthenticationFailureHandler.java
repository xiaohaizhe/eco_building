package com.giot.eco_building.config;

import com.alibaba.fastjson.JSON;
import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.constant.Constants;
import com.giot.eco_building.service.ActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 认证失败处理器
 */
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private ActionService actionService;

    @Autowired
    public CustomAuthenticationFailureHandler(ActionService actionService) {
        this.actionService = actionService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        actionService.add(Constants.ActionType.LOGIN, "失败");
        WebResponse response = new WebResponse();
        response.setCode(-1);
        response.setMessage(e.getMessage());
        httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        httpServletResponse.getWriter().write(JSON.toJSONString(response));
    }

}