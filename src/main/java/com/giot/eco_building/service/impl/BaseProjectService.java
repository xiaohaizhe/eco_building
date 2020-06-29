package com.giot.eco_building.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.constant.Constants;
import com.giot.eco_building.entity.Project;
import com.giot.eco_building.model.ProjectData;
import com.giot.eco_building.repository.ProjectRepository;
import com.giot.eco_building.service.ProjectDataService;
import com.giot.eco_building.service.ProjectService;
import com.giot.eco_building.utils.ExcelUtil;
import com.giot.eco_building.utils.HttpUtil;
import com.giot.eco_building.utils.UpdateUtil;
import jdk.nashorn.internal.objects.annotations.Setter;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * @Author: pyt
 * @Date: 2020/6/11 9:35
 * @Description:
 */
@Service
public class BaseProjectService implements ProjectService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${baidu.address.url}")
    private String MAP_URL;
    @Value("${baidu.address.ak}")
    private String AK;

    private ProjectRepository projectRepository;

    private ExcelUtil excelUtil;

    private ProjectDataService projectDataService;

    @Autowired
    public void setProjectDataService(ProjectDataService projectDataService) {
        this.projectDataService = projectDataService;
    }

    @Autowired
    public void setExcelUtil(ExcelUtil excelUtil) {
        this.excelUtil = excelUtil;
    }

    @Autowired
    public void setProjectRepository(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * 检查项目名是否已存在
     * true-存在
     * false-不存在
     *
     * @param name
     * @return
     */
    private boolean projectNameExist(String name) {
        Project project = projectRepository.findByName(name);
        return (project != null);
    }

    /**
     * 插入项目基础数据-根据项目名判断是否已存在：
     * -不存在项目--直接存储
     * -已存在项目--基础数据丢弃
     * @param projectList
     */
    @Transactional
    public void insertAll(List<Project> projectList) {
        Set<String> projectNames = new HashSet<>();
        List<Project> projectListResult = new ArrayList<>();
        for (Project project : projectList) {
            if (project != null && project.getName() != null) {
                String projectName = project.getName();
                if (!"".equals(projectName) &&
                        !projectNames.contains(projectName) &&
                        !projectNameExist(projectName)) {
                    logger.info("项目：{}正常", projectName);
                    projectNames.add(projectName);
                    project.setDelStatus(Constants.DelStatus.NORMAL.isValue());
                    projectListResult.add(project);
                } else {
                    logger.error("项目：{}，已存在", projectName);
                }
            } else {
                logger.error("项目参数不完整（项目名不存在）");
            }
        }
        if (projectListResult.size() > 0) {
            logger.info("对以上正常项目执行保存操作>>>>>>>>>>>>>>>>");
            projectRepository.saveAll(projectListResult);
            logger.info("<<<<<<<<<<<<<<<<<<<保存结束");
        }
    }

    /**
     * 根据项目地址
     * 获取项目所在经纬度
     *
     * @param project
     * @return
     */
    /*@Deprecated
    private void setLocation(Project project) throws IOException {
        String address = project.getAddress();
        if ((project.getLongitude() == null || project.getLatitude() == null)
                && !"".equals(address)) {
            Map<String, Object> params = new HashMap<>();
            params.put("address", address);
            params.put("output", "json");
            params.put("ak", AK);
            JSONObject object = HttpUtil.get(MAP_URL, params);
            int status = object.getInteger("status");
            if (status == 0) {
                JSONObject result = object.getJSONObject("result");
                JSONObject location = result.getJSONObject("location");
                float lng = location.getFloat("lng");
                float lat = location.getFloat("lat");
                project.setLongitude(lng);
                project.setLatitude(lat);
            }
        }
    }*/

    /**
     * 保存新项目
     *
     * @param project
     * @return
     */
    @Override
    @Transactional
    public boolean insert(Project project) {
        if (project != null &&
                project.getName() != null &&
                !"".equals(project.getName()) &&
                !projectNameExist(project.getName())) {
            project.setDelStatus(Constants.DelStatus.NORMAL.isValue());
            projectRepository.save(project);
            return true;
        }
        return false;
    }

    /**
     * 更新项目
     * 根据项目id
     * 更新其他非空数据
     *
     * @param project
     * @return
     */
    @Override
    @Transactional
    public boolean update(Project project) {
        if (project != null &&
                project.getId() != null &&
                !"".equals(project.getId())) {
            Project projectOld = projectRepository.findById(project.getId()).orElse(null);
            if (projectOld != null) {
                UpdateUtil.copyNullProperties(projectOld, project);
                projectRepository.saveAndFlush(project);
                return true;
            }
        }
        return false;
    }

    @Override
    public WebResponse importExcel(MultipartFile file, HttpServletRequest request) throws IOException, ParseException {
        logger.info("start deal witn excel.....");
        Workbook wb = excelUtil.getWorkbook(file);
        if (wb != null) {
            int sheetNum = wb.getNumberOfSheets();
            logger.info("{}文件共有{}页", file.getOriginalFilename(), sheetNum);
            for (int i = 0; i < sheetNum; i++) {
                Sheet sheet = wb.getSheetAt(i);
                logger.info("开始处理第{}页数据", (i + 1));
                //获取项目基础数据和日期数据
                List<Map<Integer, Object>> projectMapList = excelUtil.dealWithSheet(sheet);
                List<Project> projects = new ArrayList<>();
                List<ProjectData> projectDataList = new ArrayList<>();
                for (Map<Integer, Object> map :
                        projectMapList) {
                    Map<String, Object> resultMap = excelUtil.mapToProject(map);
                    Project project = (Project) resultMap.get("project");
                    projects.add(project);

                    List<ProjectData> dataList = (List<ProjectData>) resultMap.get("data");
                    projectDataList.addAll(dataList);
                }
                logger.info("开始存储第{}页项目基础数据>>>>>>>>>>>>>>>>>>>", (i + 1));
                insertAll(projects);
                logger.info("<<<<<<<<<<<<<<<<第{}页项目基础数据存储结束", (i + 1));
                logger.info("开始存储第{}页项目数据>>>>>>>>>>>>>>>>>>>", (i + 1));
                projectDataService.insertAll(projectDataList);
                logger.info("<<<<<<<<<<<<<<<<第{}页项目数据存储结束", (i + 1));
                logger.info("结束处理第{}页数据", (i + 1));
            }
        }
        logger.info("end up dealing witn excel.....");
        return WebResponse.success();
    }
}
