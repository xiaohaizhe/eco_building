package com.giot.eco_building.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-11-16
 * Time: 11:30
 */
@Entity
@Data
@Table(name = "research_project")
@EntityListeners(AuditingEntityListener.class)
public class ReseachProject implements Serializable {
    private static final long serialVersionUID = 5279908635651554556L;
    @Id
    @GeneratedValue(generator = "SnowflakeId")
    @GenericGenerator(name = "SnowflakeId", strategy = "com.giot.eco_building.utils.SnowflakeId")
    private Long id;
    /**
     * 编号
     */
    private String serialNumber;
    /**
     * 项目名称
     */
    private String name;
    /**
     * 项目地址
     */
    private String address;
    /**
     * 建设单位
     */
    private String constractor;
    /**
     * 经度
     */
    private Double longitude;
    /**
     * 纬度
     */
    private Double latitude;
    /**
     * 设计单位
     */
    private String designer;
    /**
     * 施工单位
     */
    private String constructor;
    /**
     * 监理单位
     */
    private String supervisor;

    /**
     * 建成时间
     */
    @Column(name = "built_time")
    private Date builtTime;
    /**
     * 建筑类型
     */
    private String type;

    public void setType(String type) {
        String t = "其他";
        String[] types = {"办公", "住宅", "商场", "文化教育", "餐饮", "医院", "酒店", "其他"};
        if (!StringUtils.isEmpty(type)) {
            for (int i = 0; i < types.length; i++) {
                if (type.equals(types[i])) {
                    t = type;
                    break;
                }
            }
        }
        this.type = type;
    }

    /**
     * 建筑楼栋数量
     */
    private Integer numberOfBuildings;
    /**
     * 建筑楼栋信息
     */
    @Column(name = "building_info")
    private String buildingInfo;
    /**
     * 建筑高度
     */
    private Double height;
    /**
     * 总面积
     */
    @Column(name = "total_area")
    private Double totalArea;

    /**
     * 地上面积
     */
    @Column(name = "aboveground_area")
    private Double abovegroundArea;
    /**
     * 地下面积
     */
    @Column(name = "underground_area")
    private Double undergroundArea;

    /**
     * 空调面积
     */
    @Column(name = "air_condition_area")
    private Double airConditionArea;
    /**
     * 地上建筑层数
     */
    @Column(name = "aboveground_floor")
    private Integer abovegroundFloor;
    /**
     * 地下建筑层数
     */
    @Column(name = "underground_floor")
    private Integer undergroundFloor;
    /**
     * 主要遮阳形式
     */
    private String sunshade;
    /**
     * 有无楼宇自控系统
     */
    private Boolean hasBas;
    /**
     * 有无能耗计量/远传电表数据
     */
    private Boolean hasEnergyonsumptionMeasurement;
    /**
     * 有无用水计量/远传水表数据
     */
    private Boolean hasWaterMetering;
    /**
     * 窗户/幕墙是否可开启
     */
    private Boolean windowIsOpen;
    /**
     * 空调系统形式
     */
    private String airConditioningSystem;
    /**
     * 新风系统形式
     */
    private String CDOAS;
    /**
     * 末端用户数量
     */
    private Integer numberOfEndUsers;
    /**
     * 暖通设备概况及信息
     */
    @Column(columnDefinition = "varchar(1000) default 'LTD'")
    private String overviewOfHVACEquipment;
    /**
     * 电器设备概况及信息
     */
    @Column(columnDefinition = "varchar(1000) default 'LTD'")
    private String overviewOfElectricalEquipment;
    /**
     * 给排水系统概况
     */
    @Column(columnDefinition = "varchar(1000) default 'LTD'")
    private String overviewOfWaterSupplyAndDrainageSystem;
    /**
     * 近三年能耗数据
     */
    @Column(columnDefinition = "varchar(1000) default 'LTD'")
    private String energyConsumptionDataInRecentThreeYears;
    /**
     * 是否建议改造
     */
    private Boolean isItSuggestedToTransform;
    /**
     * 改造建议
     */
    @Column(columnDefinition = "varchar(1000) default 'LTD'")
    private String transformationSuggestions;
    /**
     * 建筑整体照片
     */
    private String overallPhotoUrl;
    /**
     * 空调机房照片
     */
    private String airConditioningRoomPhotoUrl;
    /**
     * 主机照片
     */
    private String hostPhotoUrl;
    /**
     * 水泵照片
     */
    private String waterPumpPhotoUrl;
    /**
     * 冷却塔照片
     */
    private String coolingTowerPhotoUrl;
    /**
     * 空调箱照片
     */
    private String airConditioningBoxPhotoUrl;
    /**
     * 末端设备照片
     */
    private String terminalEquipmentPhotoUrl;

    /**
     * 单位面积电耗
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Column(name = "power_consumption_per_unit_area")
    private Double powerConsumptionPerUnitArea;
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
}
