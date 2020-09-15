package com.giot.eco_building.service.impl;

import com.giot.eco_building.EcoBuildingApplication;
import com.giot.eco_building.service.MapService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-09-14
 * Time: 16:03
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EcoBuildingApplication.class)
@EnableAutoConfiguration
public class BaseMapServiceTest {
    @Autowired
    private MapService mapService;

    @Test
    public void getPoiId() {
        List<String> res = mapService.getPoiId(31.89508803, 120.3144302, "江阴市皇达五金机电市场有限公司");
        for (String s :
                res) {
            System.out.println(s);
        }

    }

    @Test
    public void getDistrictLocation() {
        String poiid = "B0FFLHVJLJ";
        mapService.getDistrictLocation(poiid);
    }
}