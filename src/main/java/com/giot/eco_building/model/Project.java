package com.giot.eco_building.model;

import com.giot.eco_building.constant.Constants;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import java.text.CollationElementIterator;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-09-12
 * Time: 23:00
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Project {
    //    @CsvBindByPosition(position = 0)
    @CsvBindByName(column = "编号")
    private String serialNumber;

    //    @CsvBindByPosition(position = 1)
    @CsvBindByName(column = "项目名称")
    private String name;

    //    @CsvBindByPosition(position = 2)
    @CsvBindByName(column = "工程名称")
    private String projectName;

    //    @CsvBindByPosition(position = 3)
    @CsvBindByName(column = "建设单位")
    private String contractor;

    //    @CsvBindByPosition(position = 4)
    @CsvBindByName(column = "面积")
    private Double area;

    @CsvBindByName(column = "竣工日期")
//    @CsvBindByPosition(position = 5)
    @CsvDate("yyyy/MM/dd")
    private Date builtTime;

    /**
     * 建筑类型
     */
//    @CsvBindByPosition(position = 6)
    @CsvBindByName(column = "建筑主要类型")
    private String architecturalType;

    @CsvBindByName(column = "省")
//    @CsvBindByPosition(position = 7)
    private String province;

    /**
     * 市
     */
    @CsvBindByName(column = "市")
//    @CsvBindByPosition(position = 8)
    private String city;

    /**
     * 区县
     */
    @CsvBindByName(column = "区县")
//    @CsvBindByPosition(position = 9)
    private String district;

    @CsvBindByName(column = "详细地址")
//    @CsvBindByPosition(position = 10)
    private String address;
    /**
     * 纬度
     */
    @CsvBindByName(column = "纬度")
//    @CsvBindByPosition(position = 12)
    private Double latitude;
    /**
     * 经度
     */
    @CsvBindByName(column = "经度")
//    @CsvBindByPosition(position = 11)
    private Double longitude;


    /**
     * 楼层
     */
    @CsvBindByName(column = "层数")
    private Integer floor;
    /**
     * 项目概况图
     */
    @CsvBindByName(column = "项目概况图")
    private String imgUrl;

    /**
     * 绿建等级：
     * 0星-0
     * 1星-1
     * 2星-2
     * 3星-3
     * 未知-4
     */
    @CsvBindByName(column = "绿建星级")
    private String gbes;
    /**
     * 节能标准：
     * 不执行节能标准-0
     * 50%-1
     * 65%-2
     * 75%以上-3
     * 未知-4
     */
    @CsvBindByName(column = "执行节能标准")
    private String energySavingStandard;
    /**
     * 是否经过节能改造：
     * 是
     * 否
     * 未知
     */
    @CsvBindByName(column = "是否经过节能改造")
    private String energySavingTransformationOrNot;
    /**
     * 供暖方式：
     * 集中供暖
     * 分户供暖
     * 无供暖
     * 未知
     */
    @CsvBindByName(column = "供暖方式")
    private String heatingMode;
    /**
     * 供冷方式：
     * 集中供冷
     * 分户供冷
     * 无供冷
     * 未知
     */
    @CsvBindByName(column = "供冷方式")
    private String coolingMode;
    /**
     * 是否利用可再生资源：
     * 否
     * 太阳能
     * 浅层地热能
     * 未知
     */
    @CsvBindByName(column = "是否利用可再生资源")
    private String whetherToUseRenewableResources;

    public com.giot.eco_building.entity.Project getEntity() {
        com.giot.eco_building.entity.Project entity = new com.giot.eco_building.entity.Project();
        entity.setSerialNumber(this.serialNumber);
        entity.setName(this.name);
        entity.setProjectName(this.projectName);
        entity.setContractor(this.contractor);
        entity.setArea(this.area);
        entity.setBuiltTime(this.builtTime);
        entity.setArchitecturalType(this.architecturalType);
        entity.setProvince(this.province);
        entity.setCity(this.city);
        entity.setDistrict(this.district);
        entity.setAddress(this.address);
        entity.setLongitude(this.longitude);
        entity.setLatitude(this.latitude);
        entity.setFloor(this.floor);
        entity.setImgUrl(this.imgUrl);
        entity.setGbes(this.gbes);
        entity.setEnergySavingStandard(this.energySavingStandard);
        entity.setEnergySavingTransformationOrNot(this.energySavingTransformationOrNot);
        entity.setHeatingMode(this.heatingMode);
        entity.setCoolingMode(this.coolingMode);
        entity.setWhetherToUseRenewableResources(this.whetherToUseRenewableResources);
        entity.setDelStatus(Constants.DelStatus.NORMAL.isValue());
        return entity;
    }
}
