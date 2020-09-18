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
    public void getProvinces() {
        List<String> result = projectRepository.findDistinctCityByProvince("江苏省");
        for (String s :
                result) {
            System.out.println(s);
        }
    }


    @Test
    public void getAddress() {
//        System.out.println(projectService.getAddress());
    }

    @Test
    public void updateData() {
//        List<DataModel> dataModelList = new ArrayList<>();
//        List<Project> projectList = projectRepository.findAll();
//        double value = 200000;
//        for (Project project :
//                projectList) {
//            DataModel model = new DataModel();
//            model.setProjectId(project.getId());
//            model.setTimeType("年");
//            model.setType("电");
//            List<Map<String, Double>> list = new ArrayList<>();
//            Map<String, Double> map = new HashMap<>();
//            map.put("2020", value);
//            list.add(map);
//            model.setDataMap(list);
//            value += 10000;
//            dataModelList.add(model);
//        }
//        projectService.updateData(dataModelList);

    }
}