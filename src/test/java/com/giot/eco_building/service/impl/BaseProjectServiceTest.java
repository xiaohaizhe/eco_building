package com.giot.eco_building.service.impl;

import com.giot.eco_building.EcoBuildingApplication;
import com.giot.eco_building.bean.WebResponse;
import com.giot.eco_building.constant.Constants;
import com.giot.eco_building.entity.Project;
import com.giot.eco_building.repository.ProjectRepository;
import com.giot.eco_building.service.ProjectService;
import com.giot.eco_building.utils.ProjectCsvToBeanFilter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;

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
    @Qualifier("BaseProjectService")
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

    @Test
    public void updatelatestYearData() throws ParseException {
//        projectService.updatelatestYearData();
    }

    @Test
    public void recoverProjectData() throws FileNotFoundException, UnsupportedEncodingException {
        /*File file = new File("C:\\Users\\Ting\\Documents\\WeChat Files\\wxid_cgvuyra5op9q22\\FileStorage\\File\\2020-10\\编码20200914(已更正)_2(1).csv");
        //1.默认使用GBK编码
        String code = "GBK";
        //2.解析csv
        InputStreamReader is = new InputStreamReader(new FileInputStream(file), code);
        HeaderColumnNameMappingStrategy strategy = new HeaderColumnNameMappingStrategy();
        strategy.setType(com.giot.eco_building.model.Project.class);
        List<com.giot.eco_building.model.Project> projects = new CsvToBeanBuilder(is)
                .withType(com.giot.eco_building.model.Project.class)
                .withFilter(new ProjectCsvToBeanFilter())
                .withSeparator(',')
//                    .withSkipLines(1)
                .withMappingStrategy(strategy)
                .withIgnoreQuotations(true)
                .build()
                .parse();
        for (com.giot.eco_building.model.Project project :
                projects) {
            String serialNumber = project.getSerialNumber();
            String architecturalType = project.getArchitecturalType();
            Optional<Project> optional = projectRepository.findBySerialNumberAndDelStatus(serialNumber, false);
            if (optional.isPresent()) {
                Project project1 = optional.get();
                project1.setArchitecturalType(architecturalType);
                projectRepository.saveAndFlush(project1);
            }
        }*/
    }

    @Test
    public void deleteProjectData() {
        String[] serialNumbers = {
                "0510-2004-058Y", "0510-2012-015Y", "0510-2016-010", "0510-2007-042", "0510-2005-041",
                "0510-2007-012Y", "0510-2014-013Y", "0510-2016-006Y", "0510-2010-072Y", "0510-2005-027Y",
                "0510-2010-054", "0510-2016-007", "0510-2011-013", "0510-2016-021", "0510-2003-001Y",
                "0510-2006-043", "0510-2012-032", "0510-2015-014", "0510-2014-018", "0510-2013-033",
                "0510-2010-019", "0510-2016-003", "0510-2014-021", "0510-2009-007Y", "0510-2007-001",
                "0510-2014-030", "0510-2012-002", "0510-2015-001", "0510-2004-039", "0510-2007-019",
                "0510-2011-033", "0510-2002-027", "0510-2005-046"};
        for (int i = 0; i < serialNumbers.length; i++) {
            String serialNumber = serialNumbers[i];
            Optional<Project> optionalProject = projectRepository.findBySerialNumberAndDelStatus(serialNumber, Constants.DelStatus.NORMAL.isValue());
            if (optionalProject.isPresent()) {
                Project project = optionalProject.get();
                project.setDelStatus(Constants.DelStatus.DELETE.isValue());
                projectRepository.saveAndFlush(project);
            }
        }
    }
}