package com.giot.eco_building.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-10-16
 * Time: 10:18
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnergySortModel {
    private String name;
    private Double value;
    private String type;
}
