package com.giot.eco_building.service.impl;

import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.constant.Constants;
import com.giot.eco_building.entity.Project;
import com.giot.eco_building.model.ProjectData;
import com.giot.eco_building.repository.ProjectDataRepository;
import com.giot.eco_building.repository.ProjectRepository;
import com.giot.eco_building.service.ProjectDataService;
import com.giot.eco_building.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: pyt
 * @Date: 2020/6/29 11:04
 * @Description:
 */
@Service
@Transactional
public class BaseProjectDataService implements ProjectDataService {
    private ProjectDataRepository projectDataRepository;

    private ProjectRepository projectRepository;

    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Autowired
    public void setProjectDataRepository(ProjectDataRepository projectDataRepository) {
        this.projectDataRepository = projectDataRepository;
    }

    private Long getProejctIdByProjectName(String projectName) {
        Project project = projectRepository.findByName(projectName);
        if (project == null) return null;
        return project.getId();
    }


    /**
     * 项目数据：
     * 存在-更新
     * 不存在-存储
     *
     * @param projectDataList
     */
    @Override
    public void insertAll(List<ProjectData> projectDataList) {
        //projectId-projectName Map存储
        Map<String, Long> projectIdMap = new HashMap<>();
        List<com.giot.eco_building.entity.ProjectData> realDataList = new ArrayList<>();
        for (ProjectData data :
                projectDataList) {
            //1.获取projectId
            String projectName = data.getProjectName();
            Long projectId = projectIdMap.get(projectName);
            if (projectId == null) {
                projectId = getProejctIdByProjectName(projectName);

            }
            //2.存储数据
            if (projectId != null) {
                projectIdMap.put(projectName, projectId);
                Optional<com.giot.eco_building.entity.ProjectData> optional =
                        projectDataRepository.findByProjectIdAndActualDateAndTypeAndIsMonth(projectId, data.getDate(),
                                data.getProjectType(), data.getIsMonth());

                com.giot.eco_building.entity.ProjectData projectData;
                if (optional.isPresent()) {
                    projectData = optional.get();
                } else {
                    projectData = new com.giot.eco_building.entity.ProjectData();
                    projectData.setProjectId(projectId);
                    projectData.setType(data.getProjectType());
                    projectData.setIsMonth(data.getIsMonth());
                    projectData.setActualDate(data.getDate());
                }
                projectData.setValue(data.getValue());
                projectData.setDelStatus(Constants.DelStatus.NORMAL.isValue());
                realDataList.add(projectData);
            }

        }
        projectDataRepository.saveAll(realDataList);
    }

    @Override
    public com.giot.eco_building.entity.ProjectData getLatestYearData(Long projectId, Integer type) {
        com.giot.eco_building.entity.ProjectData projectData =
                projectDataRepository.findTopByProjectIdAndIsMonthAndTypeAndValueGreaterThanOrderByActualDateDesc(projectId, false, type, (double) 0);
        return projectData;
    }

    @Override
    public WebResponse getElecDataByProjectIdAndMonth(Long projectId, String start, String end) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date sdate = new Date();
        Date edate = new Date();
        sdate = sdf.parse(start);
        edate = sdf.parse(end);
        List<com.giot.eco_building.entity.ProjectData> projectDataList = projectDataRepository.findByProjectIdAndIsMonthAndTypeAndActualDateBetween(projectId, Constants.DataType.ELECTRICITY.getCode(), true, sdate, edate);
        return WebResponse.success(projectDataList);
    }
}
