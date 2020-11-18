package com.giot.eco_building.model;

import com.giot.eco_building.entity.ReseachProject;
import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-11-17
 * Time: 9:45
 */
@Data
public class ReseachProjectModel {
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
    private Date builtTime;
    /**
     * 建筑楼栋信息
     */
    private String buildingInfo;
    /**
     * 建筑高度
     */
    private Double height;
    /**
     * 总面积
     */
    private Double totalArea;

    /**
     * 地上面积
     */
    private Double abovegroundArea;
    /**
     * 地下面积
     */
    private Double undergroundArea;

    /**
     * 空调面积
     */
    private Double airConditionArea;
    /**
     * 地上建筑层数
     */
    private Integer abovegroundFloor;
    /**
     * 地下建筑层数
     */
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
    private String overviewOfHVACEquipment;
    /**
     * 电器设备概况及信息
     */
    private String overviewOfElectricalEquipment;
    /**
     * 给排水系统概况
     */
    private String overviewOfWaterSupplyAndDrainageSystem;
    /**
     * 近三年能耗数据
     */
    private String energyConsumptionDataInRecentThreeYears;
    /**
     * 是否建议改造
     */
    private Boolean isItSuggestedToTransform;
    /**
     * 改造建议
     */
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

    public ReseachProjectModel() {
    }

    public ReseachProjectModel(String serialNumber, String name, String address, String constractor, Double longitude, Double latitude,
                               String designer, String constructor, String supervisor, Date builtTime, String buildingInfo,
                               Double height, Double totalArea, Double abovegroundArea, Double undergroundArea, Double airConditionArea,
                               Integer abovegroundFloor, Integer undergroundFloor, String sunshade,
                               Boolean hasBas, Boolean hasEnergyonsumptionMeasurement, Boolean hasWaterMetering,
                               Boolean windowIsOpen, String CDOAS, Integer numberOfEndUsers, String overviewOfHVACEquipment,
                               String overviewOfElectricalEquipment, String overviewOfWaterSupplyAndDrainageSystem,
                               String energyConsumptionDataInRecentThreeYears, Boolean isItSuggestedToTransform,
                               String transformationSuggestions, String overallPhotoUrl, String airConditioningRoomPhotoUrl,
                               String hostPhotoUrl, String waterPumpPhotoUrl, String coolingTowerPhotoUrl,
                               String airConditioningBoxPhotoUrl, String terminalEquipmentPhotoUrl,
                               String airConditioningSystem) {
        this.serialNumber = serialNumber;
        this.name = name;
        this.address = address;
        this.constractor = constractor;
        this.longitude = longitude;
        this.latitude = latitude;
        this.designer = designer;
        this.constructor = constructor;
        this.supervisor = supervisor;
        this.builtTime = builtTime;
        this.buildingInfo = buildingInfo;
        this.height = height;
        this.totalArea = totalArea;
        this.abovegroundArea = abovegroundArea;
        this.undergroundArea = undergroundArea;
        this.airConditionArea = airConditionArea;
        this.abovegroundFloor = abovegroundFloor;
        this.undergroundFloor = undergroundFloor;
        this.sunshade = sunshade;
        this.hasBas = hasBas;
        this.hasEnergyonsumptionMeasurement = hasEnergyonsumptionMeasurement;
        this.hasWaterMetering = hasWaterMetering;
        this.windowIsOpen = windowIsOpen;
        this.CDOAS = CDOAS;
        this.numberOfEndUsers = numberOfEndUsers;
        this.overviewOfHVACEquipment = overviewOfHVACEquipment;
        this.overviewOfElectricalEquipment = overviewOfElectricalEquipment;
        this.overviewOfWaterSupplyAndDrainageSystem = overviewOfWaterSupplyAndDrainageSystem;
        this.energyConsumptionDataInRecentThreeYears = energyConsumptionDataInRecentThreeYears;
        this.isItSuggestedToTransform = isItSuggestedToTransform;
        this.transformationSuggestions = transformationSuggestions;
        this.overallPhotoUrl = overallPhotoUrl;
        this.airConditioningRoomPhotoUrl = airConditioningRoomPhotoUrl;
        this.hostPhotoUrl = hostPhotoUrl;
        this.waterPumpPhotoUrl = waterPumpPhotoUrl;
        this.coolingTowerPhotoUrl = coolingTowerPhotoUrl;
        this.airConditioningBoxPhotoUrl = airConditioningBoxPhotoUrl;
        this.terminalEquipmentPhotoUrl = terminalEquipmentPhotoUrl;
        this.airConditioningSystem = airConditioningSystem;
    }

    public ReseachProject toEntity() {
        ReseachProject project = new ReseachProject();
        project.setSerialNumber(this.serialNumber);
        project.setName(this.name);
        project.setAddress(this.address);
        project.setConstractor(this.constractor);
        project.setLongitude(this.longitude);
        project.setLatitude(this.latitude);
        project.setDesigner(this.designer);
        project.setConstructor(this.constructor);
        project.setSupervisor(this.supervisor);
        project.setBuiltTime(this.builtTime);
        project.setBuildingInfo(this.buildingInfo);
        project.setHeight(this.height);
        project.setTotalArea(this.totalArea);
        project.setAbovegroundArea(this.abovegroundArea);
        project.setUndergroundArea(this.undergroundArea);
        project.setAirConditionArea(this.airConditionArea);
        project.setAbovegroundFloor(this.abovegroundFloor);
        project.setUndergroundFloor(this.undergroundFloor);
        project.setSunshade(this.sunshade);
        project.setHasBas(this.hasBas);
        project.setHasEnergyonsumptionMeasurement(this.hasEnergyonsumptionMeasurement);
        project.setHasWaterMetering(this.hasWaterMetering);
        project.setWindowIsOpen(this.windowIsOpen);
        project.setCDOAS(this.CDOAS);
        project.setNumberOfEndUsers(this.numberOfEndUsers);
        project.setOverviewOfHVACEquipment(this.overviewOfHVACEquipment);
        project.setOverviewOfElectricalEquipment(this.overviewOfElectricalEquipment);
        project.setOverviewOfWaterSupplyAndDrainageSystem(this.overviewOfWaterSupplyAndDrainageSystem);
        project.setEnergyConsumptionDataInRecentThreeYears(this.energyConsumptionDataInRecentThreeYears);
        project.setIsItSuggestedToTransform(this.isItSuggestedToTransform);
        project.setTransformationSuggestions(this.transformationSuggestions);
        project.setOverallPhotoUrl(this.overallPhotoUrl);
        project.setAirConditioningRoomPhotoUrl(this.airConditioningRoomPhotoUrl);
        project.setHostPhotoUrl(this.hostPhotoUrl);
        project.setWaterPumpPhotoUrl(this.waterPumpPhotoUrl);
        project.setCoolingTowerPhotoUrl(this.coolingTowerPhotoUrl);
        project.setAirConditioningBoxPhotoUrl(this.airConditioningBoxPhotoUrl);
        project.setTerminalEquipmentPhotoUrl(this.terminalEquipmentPhotoUrl);
        project.setAirConditioningSystem(this.airConditioningSystem);
        return project;
    }
}
