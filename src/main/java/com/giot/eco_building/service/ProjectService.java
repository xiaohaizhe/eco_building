package com.giot.eco_building.service;

import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.entity.Project;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * @Author: pyt
 * @Date: 2020/6/11 9:33
 * @Description:
 */
public interface ProjectService {
    boolean insert(Project project) throws IOException;

    void insertAll(List<Project> projectList);

    void reDealWithProjectAddress();

    void latestYearData();

    boolean update(Project project);

    WebResponse JiangSuElecTop10();

    WebResponse JiangSuTop5();


    WebResponse getAddress();

    WebResponse uploadPic(MultipartFile file, HttpServletRequest request) throws IOException;

    WebResponse importExcel(MultipartFile file, HttpServletRequest request) throws IOException, ParseException;

    WebResponse screen(String province, String city, String district, String street,
                       //多选
                       String[] architecturalType, Integer[] gbes, Integer[] energySavingStandard,
                       Integer[] energySavingTransformationOrNot, Integer[] HeatingMode, Integer[] CoolingMode, Integer[] WhetherToUseRenewableResources,
                       //范围
                       Double[] area, Integer[] floor, String[] date,
                       Double[] powerConsumptionPerUnitArea, Double[] gasConsumptionPerUnitArea, Double[] waterConsumptionPerUnitArea);

    WebResponse projectDetail(Long projectId);

}
