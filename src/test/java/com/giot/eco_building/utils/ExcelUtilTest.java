package com.giot.eco_building.utils;

import com.giot.eco_building.EcoBuildingApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * @Author: pyt
 * @Date: 2020/6/12 17:38
 * @Description:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EcoBuildingApplication.class)
@EnableAutoConfiguration
public class ExcelUtilTest {
    @Autowired
    private ExcelUtil util;

    @Test
    public void dealWithExcelFile() throws IOException {
//        util.dealWithExcelFile("D:\\兼职\\建筑节能\\示例表.xlsx");
    }
}