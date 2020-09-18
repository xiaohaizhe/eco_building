package com.giot.eco_building.config;

import com.alibaba.fastjson.JSON;
import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.constant.Constants;
import com.giot.eco_building.entity.User;
import com.giot.eco_building.service.ActionService;
import com.giot.eco_building.utils.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 退出成功处理器
 */
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    private ActionService actionService;

    @Autowired
    public CustomLogoutSuccessHandler(ActionService actionService) {
        this.actionService = actionService;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String sessionId = request.getRequestedSessionId();
        WebResponse httpServletResponse;
        if (sessionId != null) {
            User user = IpUtil.getUserId(sessionId);
            IpUtil.removeSession(sessionId);
            actionService.add(Constants.ActionType.LOGOUT, "成功", user);
            httpServletResponse = WebResponse.success("退出成功");
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            response.getWriter().write(JSON.toJSONString(httpServletResponse));
        } else {
            httpServletResponse = WebResponse.failure();
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
            response.getWriter().write(JSON.toJSONString(httpServletResponse));
        }

    }

}
