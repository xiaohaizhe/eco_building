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
 * Description: 终端设备
 * User: Ting
 * Date: 2020-11-17
 * Time: 18:07
 */
@Entity
@Table
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class TerminalEquipment implements Serializable {
    private static final long serialVersionUID = 7135906779601963546L;
    @Id
    @GeneratedValue(generator = "SnowflakeId")
    @GenericGenerator(name = "SnowflakeId", strategy = "com.giot.eco_building.utils.SnowflakeId")
    private Long id;

    private Long reseachProjectId;

    private String serialNumber;

    /**
     * 型号
     */
    @Excel(name = "末端设备设备型号", orderNum = "0")
    private String model;
    /**
     * 数量
     */
    @Excel(name = "末端设备数量", orderNum = "1")
    private Integer number;
    /**
     * 额定功率
     */
    @Excel(name = "末端设备额定功率(kW)", orderNum = "2")
    private Double ratedPower;
    /**
     * 额定风量
     */
    @Excel(name = "末端设备额定风量(m³/h)", orderNum = "3")
    private Double ratedAirVolume;
    /**
     * 额定制冷量
     */
    @Excel(name = "末端设备额定制冷量(kW)", orderNum = "4")
    private Double ratedCoolingCapacity;
    /**
     * 额定制热量
     */
    @Excel(name = "末端设备额定制热量(kW)", orderNum = "5")
    private Double ratedHeatingCapacity;
    /**
     * 其他
     */
    @Excel(name = "末端设备其他", orderNum = "6")
    private String other;

    public boolean isNull() {
        return (this.model == null || "/".equals(this.model))
                && this.number == null
                && this.ratedPower == null
                && this.ratedAirVolume == null
                && this.ratedCoolingCapacity == null
                && this.ratedHeatingCapacity == null
                && (this.other == null || "/".equals(this.other))
                ;
    }
}
