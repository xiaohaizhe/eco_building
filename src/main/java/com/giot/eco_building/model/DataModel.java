package com.giot.eco_building.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: pyt
 * @Date: 2020/7/13 14:03
 * @Description:
 */
@Data
public class DataModel {
    private Long projectId;
    /**
     * 水/电/气
     */
    private String type;
    /**
     * 年/月
     */
    private String timeType;
    /**
     * 数据列表
     */
    private List<Map<String, Double>> dataMap;
}
