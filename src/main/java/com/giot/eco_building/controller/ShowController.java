package com.giot.eco_building.controller;

import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.service.ProjectDataService;
import com.giot.eco_building.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

/**
 * @Author: pyt
 * @Date: 2020/7/1 9:44
 * @Description:
 */
@RestController
@RequestMapping("/show")
public class ShowController {
    private ProjectService projectService;

    private ProjectDataService projectDataService;


    @Autowired
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Autowired
    public void setProjectDataService(ProjectDataService projectDataService) {
        this.projectDataService = projectDataService;
    }
    /**
     * 地图筛选框地址获取方式一：
     * 省-0
     * 市-1
     * 区-2
     * 街道-3
     *
     * @param level
     * @param superiorDirectory
     * @return
     */
    /*@GetMapping("/getProvinces")
    public WebResponse getProvinces(Integer level, String superiorDirectory) {
        return projectService.getAddress(level, superiorDirectory);
    }*/

    /**
     * 地图筛选框地址获取方式二：
     *
     * @return
     */
    @GetMapping("/getAddressOnMap")
    public WebResponse getAddressOnMap() {
        return projectService.getAddress();
    }

    @GetMapping("/screen")
    public WebResponse screen(String province, String city, String district,
                              //多选
                              String[] architecturalType, Integer[] gbes, Integer[] energySavingStandard,
                              Integer[] energySavingTransformationOrNot, Integer[] heatingMode, Integer[] coolingMode, Integer[] whetherToUseRenewableResources,
                              //范围
                              Double[] area, Integer[] floor, String[] date,
                              Double[] powerConsumptionPerUnitArea, Double[] gasConsumptionPerUnitArea, Double[] waterConsumptionPerUnitArea) {
        return projectService.screen(province, city, district,
                architecturalType, gbes, energySavingStandard,
                energySavingTransformationOrNot, heatingMode, coolingMode, whetherToUseRenewableResources,
                area, floor, date,
                powerConsumptionPerUnitArea, gasConsumptionPerUnitArea, waterConsumptionPerUnitArea);
    }

    @GetMapping("/projectDetail")
    public WebResponse showProejctDetail(Long projectId) {
        return projectService.projectDetail(projectId);
    }

    @GetMapping("/projectData")
    public WebResponse showProjectElecDataByTime(Long projectId, String start, String end) throws ParseException {
        return projectDataService.getElecDataByProjectIdAndMonth(projectId, start, end);
    }
}