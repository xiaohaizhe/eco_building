package com.giot.eco_building.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @Author: pyt
 * @Date: 2020/7/12 12:19
 * @Description:
 */
@Data
@NoArgsConstructor
@ToString
public class CityCount implements Serializable {
    private static final long serialVersionUID = 7248933721401000428L;
    private String city;
    private Long count;

    public CityCount(String city, Long count) {
        this.city = city;
        this.count = count;
    }
}
