package com.giot.eco_building.service;

import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.entity.Project;
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

    boolean update(Project project);

    WebResponse importExcel(MultipartFile file, HttpServletRequest request) throws IOException, ParseException;
}
