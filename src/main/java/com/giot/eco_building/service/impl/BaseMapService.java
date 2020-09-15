package com.giot.eco_building.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.giot.eco_building.service.MapService;
import com.giot.eco_building.utils.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-09-14
 * Time: 16:01
 */
@Service
public class BaseMapService implements MapService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private static Map<String, String> POIID_SHAPE_MAP = new ConcurrentHashMap<>();

    @Value("${geo.key}")
    private String key;

    @Value("${geo.address.url.around}")
    private String AROUND_URL;

    @Value("${geo.address.url.detail}")
    private String DETAIL_URL;


    @Override
    public List<String> getPoiId(Double longitude, Double latitude, String name) {
        List<String> poiIds = new ArrayList<>();
        Map<String, Object> params = new HashMap<>();
        params.put("key", key);
        params.put("location", String.format("%.6f", longitude) + ',' + String.format("%.6f", latitude));
        if (name != null && !name.equals("")) params.put("keywords", name);
        try {
            JSONObject result = HttpUtil.get(AROUND_URL, params);
            logger.info("result:{}", result);
            String info;
            if (result.get("info") != null) {
                info = (String) result.get("info");
                if (info.equals("OK")) {
                    JSONArray pois = (JSONArray) result.get("pois");
                    if (pois.size() > 0) {
                        JSONObject poi = (JSONObject) pois.get(0);
                        logger.info("poi:{}", poi);
                        String poiId = poi.getString("id");
                        poiIds.add(poiId);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return poiIds;
    }

    @Override
    public String getDistrictLocation(String poiId) {
        String shape = "";
        Map<String, Object> params = new HashMap<>();
        params.put("id", poiId);
        try {
            JSONObject result = HttpUtil.get(DETAIL_URL, params);
            logger.info(result.toString());
            if (result.get("status") != null && result.getInteger("status") == 1) {
                if (result.get("data") != null) {
                    JSONObject data = (JSONObject) result.get("data");
                    if (data.get("spec") != null) {
                        JSONObject spec = (JSONObject) data.get("spec");
                        if (spec.get("mining_shape") != null) {
                            JSONObject mining_shape = (JSONObject) spec.get("mining_shape");
                            if (mining_shape.get("shape") != null) {
                                shape = mining_shape.getString("shape");
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return shape;
    }

}

