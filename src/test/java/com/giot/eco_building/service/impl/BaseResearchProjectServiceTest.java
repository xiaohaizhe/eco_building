package com.giot.eco_building.service.impl;

import com.giot.eco_building.EcoBuildingApplication;
import com.giot.eco_building.entity.LightingEquipment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

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
}