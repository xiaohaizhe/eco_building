package com.giot.eco_building.service;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-09-14
 * Time: 16:00
 */
public interface MapService {
    Set<String> getPoiId(Double longitude, Double latitude, String name, String district);

    String getDistrictLocation(String poiId);
}
