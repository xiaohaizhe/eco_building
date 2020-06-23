package com.giot.eco_building.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.constant.Constants;
import com.giot.eco_building.entity.Project;
import com.giot.eco_building.repository.ProjectRepository;
import com.giot.eco_building.service.ProjectService;
import com.giot.eco_building.utils.ExcelUtil;
import com.giot.eco_building.utils.HttpUtil;
import com.giot.eco_building.utils.UpdateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * 根据项目地址
     * 获取项目所在经纬度
     *
     * @param project
     * @return
     */
    @Deprecated
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
    }

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
    public List<String> findCityList() {
        return null;
    }

    @Override
    public WebResponse importExcel(MultipartFile file, HttpServletRequest request) throws IOException {
        excelUtil.dealWithExcelFile(file);
        return WebResponse.success();
    }
}
