package com.giot.eco_building.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.giot.eco_building.utils.StringUtil;
import com.giot.eco_building.utils.validation.LocationValidation;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: pyt
 * @Date: 2020/6/10 14:35
 * @Description:
 */
@Entity
@Table
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Project implements Serializable {
    private static final long serialVersionUID = -6036280645685673399L;

    @Id
    @GeneratedValue(generator = "SnowflakeId")
    @GenericGenerator(name = "SnowflakeId", strategy = "com.giot.eco_building.utils.SnowflakeId")
    private Long id;
    /**
     * 编号
     */
    private String serialNumber;

    private String poiId;
    /**
     * 建筑名称
     */
    private String name;

    /**
     * 建筑单位
     */
    private String contractor;
    /**
     * 工程名称
     */
    private String projectName;
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
    @LocationValidation(min = 3.86f, max = 53.55f)
    private Double latitude;
    /**
     * 经度
     */
    @LocationValidation(min = 73.66f, max = 135.06f)
    private Double longitude;
    /**
     * 建筑类型
     */
    @Column(name = "architectural_type")
    private String architecturalType;

    public void setArchitecturalType(String architecturalType) {
        String[] types = {"办公", "商场", "文化教育", "餐饮", "医院", "酒店", "其他"};
        String type = "其他";
        if (!StringUtils.isEmpty(architecturalType)) {
            for (int i = 0; i < types.length; i++) {
                if (architecturalType.equals(types[i])) {
                    type = architecturalType;
                    break;
                }
            }
        }
        this.architecturalType = type;
    }

    /**
     * 建筑面积
     */
    private Double area;
    /**
     * 建成时间
     */
    @Column(name = "built_time")
    private Date builtTime;
    /**
     * 楼层
     */
    private Integer floor;
    /**
     * 项目图片地址
     */
    @Column(name = "img_url")
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

    public void setGbes(String gbes) {
        int code = 4;
        if (!StringUtils.isEmpty(gbes)) {
            switch (gbes) {
                case "0星":
                    code = 0;
                    break;
                case "1星":
                    code = 1;
                    break;
                case "2星":
                    code = 2;
                    break;
                case "3星":
                    code = 3;
                    break;
                default:
                    code = 4;
                    break;
            }
        }
        this.gbes = code;
    }

    public void setGbes(Integer gbes) {
        this.gbes = gbes;
    }


    /**
     * 节能标准：
     * 不执行节能标准-0
     * 50%-1
     * 65%-2
     * 75%以上-3
     * 未知-4
     */
    @Column(name = "energy_saving_standard")
    private Integer energySavingStandard;

    public void setEnergySavingStandard(String energySavingStandard) {
        int code = 4;
        if (!StringUtils.isEmpty(energySavingStandard)) {
            switch (energySavingStandard) {
                case "不执行节能标准":
                    code = 0;
                    break;
                case "50%":
                    code = 1;
                    break;
                case "65%":
                    code = 2;
                    break;
                case "75%以上":
                    code = 3;
                    break;
                default:
                    code = 4;
                    break;
            }
        }
        this.energySavingStandard = code;
    }

    public void setEnergySavingStandard(Integer energySavingStandard) {
        this.energySavingStandard = energySavingStandard;
    }

    /**
     * 是否经过节能改造：
     * 是
     * 否
     * 未知
     */
    @Column(name = "energy_saving_transformation_or_not")
    private Integer energySavingTransformationOrNot;

    public void setEnergySavingTransformationOrNot(Integer energySavingTransformationOrNot) {
        this.energySavingTransformationOrNot = energySavingTransformationOrNot;
    }

    public void setEnergySavingTransformationOrNot(String energySavingTransformationOrNot) {
        int code = 2;
        if (!StringUtils.isEmpty(energySavingTransformationOrNot)) {
            switch (energySavingTransformationOrNot) {
                case "是":
                    code = 0;
                    break;
                case "否":
                    code = 1;
                    break;
                default:
                    code = 2;
                    break;
            }
        }
        this.energySavingTransformationOrNot = code;
    }

    /**
     * 供暖方式：
     * 集中供暖
     * 分户供暖
     * 无供暖
     * 未知
     */
    @Column(name = "heating_mode")
    private Integer heatingMode;

    public void setHeatingMode(Integer heatingMode) {
        this.heatingMode = heatingMode;
    }

    public void setHeatingMode(String heatingMode) {
        int code = 3;
        if (!StringUtils.isEmpty(heatingMode)) {
            switch (heatingMode) {
                case "集中供暖":
                    code = 0;
                    break;
                case "分户供暖":
                    code = 1;
                    break;
                case "无供暖":
                    code = 2;
                    break;
                default:
                    code = 3;
                    break;
            }
        }
        this.heatingMode = code;
    }

    /**
     * 供冷方式：
     * 集中供冷
     * 分户供冷
     * 无供冷
     * 未知
     */
    @Column(name = "cooling_mode")
    private Integer coolingMode;

    public void setCoolingMode(Integer coolingMode) {
        this.coolingMode = coolingMode;
    }

    public void setCoolingMode(String coolingMode) {
        int code = 3;
        if (!StringUtils.isEmpty(coolingMode)) {
            switch (coolingMode) {
                case "集中供冷":
                    code = 0;
                    break;
                case "分户供冷":
                    code = 1;
                    break;
                case "无供冷":
                    code = 2;
                    break;
                default:
                    code = 3;
                    break;
            }
        }
        this.coolingMode = code;
    }

    /**
     * 是否利用可再生资源：
     * 否
     * 太阳能
     * 浅层地热能
     * 未知
     */
    @Column(name = "whether_to_use_renewable_resources")
    private Integer whetherToUseRenewableResources;

    public void setWhetherToUseRenewableResources(Integer whetherToUseRenewableResources) {
        this.whetherToUseRenewableResources = whetherToUseRenewableResources;
    }

    public void setWhetherToUseRenewableResources(String whetherToUseRenewableResources) {
        int code = 3;
        if (!StringUtils.isEmpty(whetherToUseRenewableResources)) {
            switch (whetherToUseRenewableResources) {
                case "否":
                    code = 0;
                    break;
                case "太阳能":
                    code = 1;
                    break;
                case "浅层地热能":
                    code = 2;
                    break;
                default:
                    code = 3;
                    break;
            }
        }
        this.whetherToUseRenewableResources = code;
    }

    /**
     * 单位面积电耗
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Column(name = "power_consumption_per_unit_area")
    private Double powerConsumptionPerUnitArea;

    /**
     * 单位面积水耗
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Column(name = "water_consumption_per_unit_area")
    private Double waterConsumptionPerUnitArea;

    /**
     * 单位面积气耗
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Column(name = "gas_consumption_per_unit_area")
    private Double gasConsumptionPerUnitArea;

    /**
     * 创建时间
     */
    @JSONField(serialize = false)
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private Date created;
    /**
     * 最新修改时间
     */
//    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @LastModifiedDate
    @Column(nullable = false, name = "last_modified")
    private Date lastModified;
    /**
     * 删除标记位：0-有效，1-无效
     */
    @JSONField(serialize = false)
    @Column(nullable = false, columnDefinition = "bit default 0", name = "del_status")
    private Boolean delStatus;

    private String shape;
}
