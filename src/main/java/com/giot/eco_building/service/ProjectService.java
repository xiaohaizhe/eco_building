package com.giot.eco_building.service;

import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.entity.Project;
import com.giot.eco_building.model.DataModel;
import com.giot.eco_building.model.ProjectModel;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;


/**
 * @Author: pyt
 * @Date: 2020/6/11 9:33
 * @Description:
 */
public interface ProjectService {
    boolean insert(Project project) throws IOException;

    Map<String, String> insertAll(List<Project> projectList);

    void reDealWithProjectAddress();

    void latestYearData();

    WebResponse updateData(List<DataModel> dataModel);

    WebResponse update(ProjectModel project);

    WebResponse getDataByTime(String dataType, String timeType, Long projectId, String start, String end);

    WebResponse deleteById(Long id);

    WebResponse page(String name,
                     String province, String city, String district,
                     String architecturalType,
                     Integer number, Integer size);

    void downloadExample(HttpServletRequest request, HttpServletResponse response) throws IOException;

    WebResponse JiangSuElecTop10();

    WebResponse JiangSuTop5();


    WebResponse getAddress();

    WebResponse uploadPic(MultipartFile file, HttpServletRequest request) throws IOException;

    WebResponse importExcel(MultipartFile file, HttpServletRequest request) throws IOException, ParseException;

    WebResponse importFile(MultipartFile[] files, HttpServletRequest request) throws IOException, CsvValidationException, ParseException;

    WebResponse screen(String province, String city, String district,
                       //多选
                       String[] architecturalType, Integer[] gbes, Integer[] energySavingStandard,
                       Integer[] energySavingTransformationOrNot, Integer[] HeatingMode, Integer[] CoolingMode, Integer[] WhetherToUseRenewableResources,
                       //范围
                       Double[] area, Integer[] floor, String[] date,
                       Double[] powerConsumptionPerUnitArea, Double[] gasConsumptionPerUnitArea, Double[] waterConsumptionPerUnitArea);

    WebResponse projectDetail(Long projectId);

    WebResponse importFile(MultipartFile file, boolean isData, HttpServletRequest request) throws IOException, CsvValidationException, ParseException;

    void insertShape();

}
