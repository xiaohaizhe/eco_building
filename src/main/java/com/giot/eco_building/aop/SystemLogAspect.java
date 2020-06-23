package com.giot.eco_building.aop;

import com.alibaba.fastjson.JSONObject;
import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.constant.Constants;
import com.giot.eco_building.entity.Action;
import com.giot.eco_building.entity.User;
import com.giot.eco_building.service.ActionService;
import com.giot.eco_building.utils.IpUtil;
import org.apache.tomcat.jni.Local;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Date;

/**
 * @Author: pyt
 * @Date: 2020/6/15 14:44
 * @Description:
 */
@Aspect
@Primary
@Component
public class SystemLogAspect {
    private static final Logger logger = LoggerFactory.getLogger(SystemLogAspect.class);

    private ActionService actionService;

    @Autowired
    public void setActionService(ActionService actionService) {
        this.actionService = actionService;
    }

    @Pointcut("@annotation(com.giot.eco_building.aop.SystemControllerLog)")
    public void controllerAspect() {

    }


    @AfterReturning(pointcut = "controllerAspect()", returning = "object")
    public void doAfter(JoinPoint joinPoint, WebResponse object) throws ClassNotFoundException {
        String desc = getControllerMethodDescription(joinPoint);
        Constants.ActionType type = null;
        for (Constants.ActionType actionType :
                Constants.ActionType.values()) {
            if (desc.equals(actionType.getValue())) {
                type = actionType;
                break;
            }
        }
        if (object.getCode() == 0) {
            actionService.add(type, "成功");
        } else {
            actionService.add(type, "失败");
        }
    }

    /**
     * 获取注解中对方法的描述信息，用于controller层注解
     *
     * @param joinPoint
     * @return
     * @throws Exception
     */
    private static String getControllerMethodDescription(JoinPoint joinPoint) throws ClassNotFoundException {
        Object[] arguments = joinPoint.getArgs();
        Class targetClass = Class.forName(joinPoint.getTarget().getClass().getName());
        Method[] methods = targetClass.getMethods();
        String description = "";
        for (Method method : methods) {
            if (method.getName().equals(joinPoint.getSignature().getName())) {
                Class[] clazzs = method.getParameterTypes();
                if (clazzs.length == arguments.length) {
                    description = method.getAnnotation(SystemControllerLog.class).description();
                    break;
                }
            }
        }
        return description;
    }
}
