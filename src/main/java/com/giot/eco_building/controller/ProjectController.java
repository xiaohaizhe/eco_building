package com.giot.eco_building.controller;

import com.giot.eco_building.aop.SystemControllerLog;
import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.model.DataModel;
import com.giot.eco_building.model.ProjectModel;
import com.giot.eco_building.service.ProjectService;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

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

    @PostMapping("update")
    @SystemControllerLog(description = "项目修改")
    public WebResponse update(@RequestBody ProjectModel project) {
        return projectService.update(project);
    }

    @PostMapping("updateData")
    @SystemControllerLog(description = "项目数据修改")
    public WebResponse updateData(@RequestBody List<DataModel> dataModelList) {
        return projectService.updateData(dataModelList);
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

    @PostMapping("import")
    @SystemControllerLog(description = "上传")
    public WebResponse importFile(MultipartFile[] files, boolean isData, HttpServletRequest request) {
        try {
            return projectService.importCsv(files, isData, request);
        } catch (IOException | CsvValidationException | ParseException e) {
            return WebResponse.exception(e);
        }
    }

    @GetMapping("downloadExample")
    public void downloadExample(HttpServletRequest request, HttpServletResponse response) throws IOException {
        projectService.downloadExample(request, response);
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
                                   String province, String city, String district,
                                   String architecturalType,
                                   Integer number, Integer size) {
        return projectService.page(name, province, city, district, architecturalType, number, size);
    }

    @GetMapping("screenPage")
    public WebResponse projectcreenPage(String name,
                                   String province, String city, String district,
                                   String architecturalType,
                                   Integer number, Integer size) {
        return projectService.page(name, province, city, district, architecturalType, number, size);
    }

    @PostMapping("delete")
    @SystemControllerLog(description = "项目删除")
    public WebResponse deleteById(Long id) {
        return projectService.deleteById(id);
    }

    @GetMapping("getDataByTime")
    public WebResponse getDataByTime(String dataType, String timeType, Long projectId, String start, String end) {
        return projectService.getDataByTime(dataType, timeType, projectId, start, end);
    }

    @GetMapping("shape")
    public void updateShape(){
        projectService.insertShape();
    }

}
