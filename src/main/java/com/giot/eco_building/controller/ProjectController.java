package com.giot.eco_building.controller;

import com.giot.eco_building.aop.SystemControllerLog;
import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.entity.Project;
import com.giot.eco_building.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;

/**
 * @Author: pyt
 * @Date: 2020/6/11 17:31
 * @Description:
 */
@RestController
@RequestMapping("project")
public class ProjectController {
    private ProjectService projectService;

    @Autowired
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("importExcel")
    @SystemControllerLog(description = "上传")
    public WebResponse importExcel(MultipartFile file, HttpServletRequest request) {
        try {
            return projectService.importExcel(file, request);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return WebResponse.exception(e);
        }
    }

    @PostMapping("uploadPic")
    public WebResponse uploadPic(MultipartFile file, HttpServletRequest request) {
        try {
            return projectService.uploadPic(file, request);
        } catch (IOException e) {
            e.printStackTrace();
            return WebResponse.exception(e);
        }
    }

    @GetMapping("page")
    public WebResponse projectPage(String name,
                                   String province, String city, String district, String street,
                                   String architecturalType,
                                   Integer number, Integer size) {
        return projectService.page(name, province, city, district, street, architecturalType, number, size);
    }
}
