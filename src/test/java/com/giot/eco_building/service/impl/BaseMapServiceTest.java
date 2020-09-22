package com.giot.eco_building.service.impl;

import com.giot.eco_building.EcoBuildingApplication;
import com.giot.eco_building.service.MapService;
import com.giot.eco_building.utils.HttpUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;
import java.util.Set;

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


    }

    @Test
    public void getDistrictLocation() {
//        String ipUrl = "https://too.ueuz.com/frontapi/public/http/get_ip/index?type=-1&iptimelong=1&ipcount=1&protocol=0&areatype=1&area=&resulttype=txt&duplicate=0&separator=1&other=&show_city=0&show_carrier=0&show_expire=0&isp=-1&auth_key=375c51cbe311019924625fc6074706f0&app_key=68827d59d8784ca7eae70224d50db79d&timestamp=1600598992&sign=9521D75A22739FD9547B2A38DD969084";
//        HttpUtil.get()
        String poiid = "B01FE1ACB6";
        try {
            mapService.getDistrictLocation(poiid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}