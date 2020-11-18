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
 * Description: 主机
 * User: Ting
 * Date: 2020-11-17
 * Time: 17:34
 */
@Entity
@Table
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class MainEngine implements Serializable {
    private static final long serialVersionUID = -2575194388329518348L;
    @Id
    @GeneratedValue(generator = "SnowflakeId")
    @GenericGenerator(name = "SnowflakeId", strategy = "com.giot.eco_building.utils.SnowflakeId")
    private Long id;

    private Long reseachProjectId;

    private String serialNumber;

    /**
     * 型号
     */
    @Excel(name = "主机型号", orderNum = "0")
    private String model;
    /**
     * 数量
     */
    @Excel(name = "主机数量", orderNum = "1")
    private Integer number;
    /**
     * 额定制冷量
     */
    @Excel(name = "主机额定制冷量kW", orderNum = "2")
    private Double ratedCoolingCapacity;
    /**
     * 额定制热量
     */
    @Excel(name = "主机额定制热量", orderNum = "3")
    private Double ratedHeatingCapacity;
    /**
     * 功率
     */
    @Excel(name = "主机功率(kW)", orderNum = "4")
    private String power;
    /**
     * 热回收量
     */
    @Excel(name = "主机热回收量(kW)", orderNum = "5")
    @Column(nullable = true)
    private Double heatRecovery;
    /**
     * 厂商
     */
    @Excel(name = "主机厂商", orderNum = "6")
    private String manufacturer;
    /**
     * 机组种类
     */
    @Excel(name = "主机机组种类", orderNum = "7")
    private String unitType;

    public boolean isNull() {
        return (this.model == null || "/".equals(this.model))
                && this.number == null
                && this.ratedCoolingCapacity == null
                && this.ratedHeatingCapacity == null
                && (this.power == null || "/".equals(this.power))
                && this.heatRecovery == null
                && (this.manufacturer == null || "/".equals(this.manufacturer))
                && (this.unitType == null || "/".equals(this.unitType))
                ;
    }
}
