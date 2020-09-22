package com.giot.eco_building.service;

import com.giot.eco_building.entity.Project;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-09-14
 * Time: 16:00
 */
public interface MapService {
    String getPoiId(Project project);

    String getDistrictLocation(String poiId) throws IOException;
}
