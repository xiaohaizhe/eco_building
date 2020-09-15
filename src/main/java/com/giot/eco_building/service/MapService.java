package com.giot.eco_building.service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-09-14
 * Time: 16:00
 */
public interface MapService {
    List<String> getPoiId(Double longitude, Double latitude, String name);

    String getDistrictLocation(String poiId);
}
