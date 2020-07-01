package com.giot.eco_building.controller;

import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: pyt
 * @Date: 2020/7/1 9:44
 * @Description:
 */
@RestController
@RequestMapping("/show")
public class ShowController {
    private ProjectService projectService;


    @Autowired
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
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
    public WebResponse screen(String province, String city, String district, String street,
                       //多选
                       String[] architecturalType, Integer[] gbes, Integer[] energySavingStandard,
                       Integer[] energySavingTransformationOrNot, Integer[] HeatingMode, Integer[] CoolingMode, Integer[] WhetherToUseRenewableResources,
                       //范围
                       Double[] area, Integer[] floor, String[] date,
                       Double[] powerConsumptionPerUnitArea, Double[] gasConsumptionPerUnitArea, Double[] waterConsumptionPerUnitArea) {
        return projectService.screen(province, city, district, street,
                architecturalType, gbes, energySavingStandard,
                energySavingTransformationOrNot, HeatingMode, CoolingMode, WhetherToUseRenewableResources,
                area, floor, date,
                powerConsumptionPerUnitArea, gasConsumptionPerUnitArea, waterConsumptionPerUnitArea);
    }
}