package com.giot.eco_building.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: pyt
 * @Date: 2020/6/28 17:29
 * @Description:
 */
@Data
@NoArgsConstructor
public class ProjectData {
    private String projectName;
    /**
     * 水电气
     */
    private Integer projectType;
    /**
     * 数据类型:
     * 0-年数据
     * 1-月数据
     */
    private Boolean isMonth;
    private Date date;
    private Double value;
}
