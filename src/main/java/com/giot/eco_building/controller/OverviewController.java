package com.giot.eco_building.controller;

import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: pyt
 * @Date: 2020/7/12 9:37
 * @Description:
 */
@RestController
@RequestMapping("/overview")
public class OverviewController {
    private ProjectService projectService;

    @Autowired
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("top5")
    public WebResponse JiangSuTop5() {
        return projectService.JiangSuTop5();
    }

    @GetMapping("top10")
    public WebResponse JiangSuElecTop10() {
        return projectService.JiangSuElecTop10();
    }
}
