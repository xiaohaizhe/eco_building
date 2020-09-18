package com.giot.eco_building.model;

import com.giot.eco_building.entity.Project;
import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author: pyt
 * @Date: 2020/7/13 11:52
 * @Description:
 */
@Data
public class ProjectModel {
    private Long id;
    /**
     * 建筑名称
     */
    private String name;
    /**
     * 建筑地址
     */
    private String address;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 区
     */
    private String district;
    /**
     * 街道
     */
//    private String street;
    /**
     * 纬度
     */
    private Double latitude;
    /**
     * 经度
     */
    private Double longitude;
    /**
     * 建筑类型
     */
    private String architecturalType;

    /**
     * 建筑面积
     */
    private Double area;
    /**
     * 建成时间
     */
    private String builtTime;
    /**
     * 楼层
     */
    private Integer floor;
    /**
     * 项目图片地址
     */
    private String imgUrl;
    /**
     * 绿建等级：
     * 0星-0
     * 1星-1
     * 2星-2
     * 3星-3
     * 未知-4
     */
    private Integer gbes;

    /**
     * 节能标准：
     * 不执行节能标准-0
     * 50%-1
     * 65%-2
     * 75%以上-3
     * 未知-4
     */
    private Integer energySavingStandard;

    /**
     * 是否经过节能改造：
     * 是
     * 否
     * 未知
     */
    private Integer energySavingTransformationOrNot;


    /**
     * 供暖方式：
     * 集中供暖
     * 分户供暖
     * 无供暖
     * 未知
     */
    private Integer heatingMode;
    /**
     * 供冷方式：
     * 集中供冷
     * 分户供冷
     * 无供冷
     * 未知
     */
    private Integer coolingMode;


    /**
     * 是否利用可再生资源：
     * 否
     * 太阳能
     * 浅层地热能
     * 未知
     */
    private Integer whetherToUseRenewableResources;

    public Project getProject() {
        Project project = new Project();
        project.setId(this.id);
        project.setName(this.name);

        project.setAddress(this.address);
        project.setProvince(this.province);
        project.setCity(this.city);
        project.setDistrict(this.district);
//        project.setStreet(this.street);

        project.setLatitude(this.latitude);
        project.setLongitude(this.longitude);

        project.setArchitecturalType(this.architecturalType);
        project.setArea(this.area);

        Date bt = null;
        if (this.builtTime != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            try {
                bt = sdf.parse(this.builtTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        project.setBuiltTime(bt);

        project.setFloor(this.floor);
        project.setImgUrl(this.imgUrl);

        project.setGbes(this.gbes);
        project.setEnergySavingStandard(this.energySavingStandard);
        project.setEnergySavingTransformationOrNot(this.energySavingTransformationOrNot);
        project.setHeatingMode(this.heatingMode);
        project.setCoolingMode(this.coolingMode);
        project.setWhetherToUseRenewableResources(this.whetherToUseRenewableResources);

        return project;
    }
}
