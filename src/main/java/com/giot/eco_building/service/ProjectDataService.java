package com.giot.eco_building.service;

import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.model.ProjectData;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * @Author: pyt
 * @Date: 2020/6/29 11:04
 * @Description:
 */
public interface ProjectDataService {
    WebResponse getElecDataByProjectIdAndMonth(Long projectId, String start, String end) throws ParseException;

    void insertAll(List<ProjectData> projectDataList);

    void saveOrUpdateByProjectId(List<com.giot.eco_building.entity.ProjectData> dataList);

    com.giot.eco_building.entity.ProjectData getLatestYearData(Long projectId, Integer type);

    com.giot.eco_building.entity.ProjectData getByActualDateAndIsMonthAndType(Long projectId, Boolean isMonth, Integer type, Date date);

    List<com.giot.eco_building.entity.ProjectData> getDataByTime(Integer type, Boolean isMonth, Long projectId, Date start, Date end);
}
