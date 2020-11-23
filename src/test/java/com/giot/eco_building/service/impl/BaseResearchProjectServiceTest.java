package com.giot.eco_building.service.impl;

import com.giot.eco_building.EcoBuildingApplication;
import com.giot.eco_building.entity.ReseachProject;
import com.giot.eco_building.repository.ReseachProjectRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sun.util.resources.cldr.zu.CalendarData_zu_ZA;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-11-18
 * Time: 10:19
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EcoBuildingApplication.class)
@EnableAutoConfiguration
public class BaseResearchProjectServiceTest {
    @Autowired
    private BaseResearchProjectService baseResearchProjectService;
    @Autowired
    private ReseachProjectRepository reseachProjectRepository;

    /*@Test
    public void dealWithExcel() {
        List<LightingEquipment> list = null;
        try {
            list = baseResearchProjectService.dealWithExcel("D:\\兼职\\建筑节能\\更新\\20201115\\调研建筑项目数据库表格设计20201116(1)\\调研建筑项目数据库表格设计20201116\\子表模板\\电气子表设计_照明设备.xlsx",
                    LightingEquipment.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (LightingEquipment lightingEquipment : list) {
            System.out.println(lightingEquipment.toString());
        }
    }*/

    /*@Test
    public void deleteSymbol() throws IllegalAccessException {
        List<ReseachProject> reseachProjectList = reseachProjectRepository.findAll();
        for (ReseachProject p :
                reseachProjectList) {
            Field[] fields = ReseachProject.class.getDeclaredFields();
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++");
            for (Field field :
                    fields) {
                String name = field.getName();
                field.setAccessible(true);
                Object value = field.get(p);
                if ("/".equals(value)) {
                    System.out.println(name+":"+value);
                    field.set(p, null);
                }
            }
            reseachProjectRepository.saveAndFlush(p);
        }
    }*/
}