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
 * Description: 电器负载
 * User: Ting
 * Date: 2020-11-18
 * Time: 9:29
 */
@Entity
@Table
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class ElectricalLoad implements Serializable {
    private static final long serialVersionUID = -9047539097118727756L;
    @Id
    @GeneratedValue(generator = "SnowflakeId")
    @GenericGenerator(name = "SnowflakeId", strategy = "com.giot.eco_building.utils.SnowflakeId")
    private Long id;

    private Long reseachProjectId;

    private String serialNumber;
    /**
     * 类型
     */
    @Excel(name = "电器设备类型", orderNum = "0")
    private String type;
    /**
     * 型号
     */
    @Excel(name = "电器设备型号", orderNum = "1")
    private String model;
    /**
     * 数量
     */
    @Excel(name = "电器设备数量", orderNum = "2")
    private Integer number;
    /**
     * 额定功率
     */
    @Excel(name = "电器设备额定功率(W)", orderNum = "3")
    private Double ratedPower;
    /**
     * 厂家
     */
    @Excel(name = "电器设备厂家", orderNum = "4")
    private String manufacturer;

    public boolean isNull() {
        return (this.type == null || "/".equals(this.type))
                && (this.model == null || "/".equals(this.model))
                && this.number == null
                && this.ratedPower == null
                && (this.manufacturer == null || "/".equals(this.manufacturer))
                ;
    }
}
