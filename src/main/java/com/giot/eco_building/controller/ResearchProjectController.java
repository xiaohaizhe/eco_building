package com.giot.eco_building.controller;

import com.giot.eco_building.aop.SystemControllerLog;
import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.service.ResearchProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-11-17
 * Time: 9:48
 */
@RestController
@RequestMapping("r_project")
public class ResearchProjectController {
    private ResearchProjectService researchProjectService;

    @Autowired
    public void setResearchProjectService(ResearchProjectService researchProjectService) {
        this.researchProjectService = researchProjectService;
    }

    @PostMapping("import")
    @SystemControllerLog(description = "上传")
    public WebResponse importFile(MultipartFile[] files, HttpServletRequest request) {
        try {
            return researchProjectService.importFile(files, request);
        } catch (IOException | ParseException e) {
            return WebResponse.exception(e);
        }
    }

    @GetMapping("list")
    public WebResponse list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isItSuggestedToTransform) {
        return WebResponse.success(researchProjectService.list(name, isItSuggestedToTransform));
    }

    @GetMapping("findById")
    public WebResponse findById(Long id) {
        return WebResponse.success(researchProjectService.findById(id));
    }

    @GetMapping("getHVACEquipmentById")
    public WebResponse getHVACEquipmentById(Long id) {
        return WebResponse.success(researchProjectService.getHVACEquipmentById(id));
    }

    @GetMapping("getElectricalEquipmentById")
    public WebResponse getElectricalEquipmentById(Long id) {
        return WebResponse.success(researchProjectService.getElectricalEquipmentById(id));
    }

    @GetMapping("getWaterData")
    public WebResponse getWaterData(Long id, String start, String end) {
        try {
            return WebResponse.success(researchProjectService.getDataByTypeAndTime(id, 0, start, end));
        } catch (ParseException e) {
            return WebResponse.exception(e);
        }
    }

    @GetMapping("getElecData")
    public WebResponse getElecData(Long id, String start, String end) {
        try {
            return WebResponse.success(researchProjectService.getDataByTypeAndTime(id, 1, start, end));
        } catch (ParseException e) {
            return WebResponse.exception(e);
        }
    }

    @GetMapping("getGasData")
    public WebResponse getGasData(Long id, String start, String end) {
        try {
            return WebResponse.success(researchProjectService.getDataByTypeAndTime(id, 2, start, end));
        } catch (ParseException e) {
            return WebResponse.exception(e);
        }
    }

    @GetMapping("getHeatData")
    public WebResponse getHeatData(Long id, String start, String end) {
        try {
            return WebResponse.success(researchProjectService.getDataByTypeAndTime(id, 3, start, end));
        } catch (ParseException e) {
            return WebResponse.exception(e);
        }
    }
}
