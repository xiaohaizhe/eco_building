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
 * Description: 水泵
 * User: Ting
 * Date: 2020-11-17
 * Time: 17:46
 */
@Entity
@Table
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class WaterPump implements Serializable {
    private static final long serialVersionUID = -5708081883925104198L;
    @Id
    @GeneratedValue(generator = "SnowflakeId")
    @GenericGenerator(name = "SnowflakeId", strategy = "com.giot.eco_building.utils.SnowflakeId")
    private Long id;

    private Long reseachProjectId;

    private String serialNumber;

    /**
     * 型号
     */
    @Excel(name = "水泵型号", orderNum = "0")
    private String model;
    /**
     * 数量
     */
    @Excel(name = "水泵数量", orderNum = "1")
    private Integer number;
    /**
     * 额定流量
     */
    @Excel(name = "水泵额定流量(m³/h)", orderNum = "2")
    private Double ratedFlow;
    /**
     * 扬程
     */
    @Excel(name = "水泵扬程(m)", orderNum = "3")
    private Double lift;
    /**
     * 功率
     */
    @Excel(name = "水泵功率(kW)", orderNum = "4")
    private Double power;
    /**
     * 转速
     */
    @Excel(name = "水泵转速(rpm)", orderNum = "5")
    private Double speed;
    /**
     * 效率
     */
    @Excel(name = "水泵效率(%)", orderNum = "6")
    private Double efficiency;
    /**
     * 厂商
     */
    @Excel(name = "水泵厂商", orderNum = "7")
    private String manufacturer;
    /**
     * 用途
     */
    @Excel(name = "水泵用途", orderNum = "8")
    private String purpose;


    public boolean isNull() {
        return (this.model == null || "/".equals(this.model))
                && this.number == null
                && this.ratedFlow == null
                && this.lift == null
                && this.power == null
                && this.speed == null
                && this.efficiency == null
                && (this.manufacturer == null || "/".equals(this.manufacturer))
                && (this.purpose == null || "/".equals(this.purpose));
    }
}
