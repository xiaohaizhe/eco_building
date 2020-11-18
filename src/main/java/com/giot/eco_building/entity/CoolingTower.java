package com.giot.eco_building.entity;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description: 冷却塔
 * User: Ting
 * Date: 2020-11-17
 * Time: 17:53
 */
@Entity
@Table
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CoolingTower implements Serializable {
    private static final long serialVersionUID = 938703569689956758L;
    @Id
    @GeneratedValue(generator = "SnowflakeId")
    @GenericGenerator(name = "SnowflakeId", strategy = "com.giot.eco_building.utils.SnowflakeId")
    private Long id;

    private Long reseachProjectId;

    private String serialNumber;

    /**
     * 型号
     */
    @Excel(name = "冷却塔型号", orderNum = "0")
    private String model;
    /**
     * 数量
     */
    @Excel(name = "冷却塔数量", orderNum = "1")
    private Integer number;
    /**
     * 冷却能力
     */
    @Excel(name = "冷却塔冷却能力(kcal/h)", orderNum = "2")
    private Double coolingCapacity;
    /**
     * 冷却水量
     */
    @Excel(name = "冷却塔冷却水量(m³/h)", orderNum = "3")
    private Double coolingWaterQuantity;
    /**
     * 配套管径
     */
    @Excel(name = "冷却塔配套管径(进/出)(mm)", orderNum = "4")
    private Double matchingPipeDiameter;
    /**
     * 风机功率
     */
    @Excel(name = "冷却塔风机功率(kW)", orderNum = "5")
    private Double fanPower;
    /**
     * 厂商
     */
    @Excel(name = "冷却塔厂商", orderNum = "6")
    private String manufacturer;
    /**
     * 类型
     */
    @Excel(name = "冷却塔类型", orderNum = "7")
    private String type;

    public boolean isNull() {
        return (this.model == null || "/".equals(this.model))
                && this.number == null
                && this.coolingCapacity == null
                && this.coolingWaterQuantity == null
                && this.matchingPipeDiameter == null
                && this.fanPower == null
                && (this.manufacturer == null || "/".equals(this.manufacturer))
                && (this.type == null || "/".equals(this.type))
                ;
    }
}
