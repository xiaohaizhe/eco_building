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
 * Description: 空调箱
 * User: Ting
 * Date: 2020-11-17
 * Time: 18:02
 */
@Entity
@Table
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AirConditionBox implements Serializable {
    private static final long serialVersionUID = -8930546038033791067L;
    @Id
    @GeneratedValue(generator = "SnowflakeId")
    @GenericGenerator(name = "SnowflakeId", strategy = "com.giot.eco_building.utils.SnowflakeId")
    private Long id;

    private Long reseachProjectId;

    private String serialNumber;

    /**
     * 型号
     */
    @Excel(name = "空调箱型号", orderNum = "0")
    private String model;
    /**
     * 数量
     */
    @Excel(name = "空调箱数量", orderNum = "1")
    private Integer number;
    /**
     * 风量
     */
    @Excel(name = "空调箱风量(m³/h)", orderNum = "2")
    private Double airVolume;
    /**
     * 冷量
     */
    @Excel(name = "空调箱冷量", orderNum = "3")
    private Double coolingCapacity;
    /**
     * 热量
     */
    @Excel(name = "空调箱热量(kW)", orderNum = "4")
    private Double heatingCapacity;
    /**
     * 功率
     */
    @Excel(name = "空调箱功率(kW)", orderNum = "5")
    private Double power;
    /**
     * 余压
     */
    @Excel(name = "空调箱余压(Pa)", orderNum = "6")
    private Double residualPressure;

    public AirConditionBox(String model, Integer number, Double airVolume, Double coolingCapacity, Double heatingCapacity, Double power, Double residualPressure) {
        this.model = model;
        this.number = number;
        this.airVolume = airVolume;
        this.coolingCapacity = coolingCapacity;
        this.heatingCapacity = heatingCapacity;
        this.power = power;
        this.residualPressure = residualPressure;
    }

    public boolean isNull(){
        return (this.model == null || this.model.equals("/"))
                && this.number ==null
                && this.airVolume ==null
                && this.coolingCapacity ==null
                && this.heatingCapacity ==null
                && this.power ==null
                && this.residualPressure ==null
                ;
    }
}
