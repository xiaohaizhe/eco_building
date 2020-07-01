package com.giot.eco_building.service.impl;

import com.giot.eco_building.EcoBuildingApplication;
import com.giot.eco_building.entity.Project;
import com.giot.eco_building.repository.ProjectRepository;
import com.giot.eco_building.service.ProjectService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @Author: pyt
 * @Date: 2020/6/11 17:38
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EcoBuildingApplication.class)
@EnableAutoConfiguration
class BaseProjectServiceTest {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectRepository projectRepository;

    @Test
    void insert() throws IOException {
        Project project = new Project();
        project.setName("test");
        project.setAddress("南京市玄武区苏宁青创园");
        projectService.insert(project);
    }

    @Test
    void update() {
        Project project = new Project();
        project.setId(645131296309248l);
        project.setName("test1");
        project.setAddress("南京市玄武区苏宁青创园");
        projectService.update(project);
    }

    @Test
    void testLayFetchType() {
        List<Project> projects = projectRepository.findAll();
        for (Project pro :
                projects) {
            System.out.println(pro.toString());
            System.out.println(pro.getPhoto());
        }

    }

    @Test
    public void getProvinces() {
        List<String> result = projectRepository.findDistinctCityByProvince("江苏省");
        for (String s :
                result) {
            System.out.println(s);
        }
    }


    @Test
    public void getAddress() {
        System.out.println(projectService.getAddress());
    }
}