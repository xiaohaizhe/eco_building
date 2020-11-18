package com.giot.eco_building.model;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-11-18
 * Time: 15:10
 */
@Data
public class ResearchProjectDataModel {
    @Excel(name = "日期")
    private String date;
    @Excel(name = "用电量")
    private Double elec;
    @Excel(name = "用水量")
    private Double water;
    @Excel(name = "用气量")
    private Double gas;
    @Excel(name = "用热量")
    private Double heat;

    public boolean isNull() {
        return date == null
                && elec == null
                && water == null
                && gas == null
                && heat == null
                ;
    }

}
