package com.giot.eco_building.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.entity.ReseachProject;
import com.giot.eco_building.entity.ResearchProjectData;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-11-17
 * Time: 9:49
 */
public interface ResearchProjectService {
    WebResponse importFile(MultipartFile[] files, HttpServletRequest request) throws IOException, ParseException;

    List<ReseachProject> list(String name, Boolean isItSuggestedToTransform);

    ReseachProject findById(Long id);

    JSONObject getHVACEquipmentById(Long id);

    JSONObject getElectricalEquipmentById(Long id);

    List<ResearchProjectData> getDataByTypeAndTime(Long id, Integer type, String start, String end) throws ParseException;

    JSONArray get3YearsElecData(Long id) throws ParseException;

    WebResponse statistic();

    WebResponse energySort();

    WebResponse Top10();

}
