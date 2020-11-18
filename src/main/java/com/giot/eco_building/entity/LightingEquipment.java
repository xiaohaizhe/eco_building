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
 * Description: 照明设备
 * User: Ting
 * Date: 2020-11-18
 * Time: 9:30
 */
@Entity
@Table
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class LightingEquipment implements Serializable {
    private static final long serialVersionUID = 955600440711166357L;
    @Id
    @GeneratedValue(generator = "SnowflakeId")
    @GenericGenerator(name = "SnowflakeId", strategy = "com.giot.eco_building.utils.SnowflakeId")
    private Long id;

    private Long reseachProjectId;

    private String serialNumber;

    /**
     * 名称
     */
    @Excel(name = "照明灯具名称", orderNum = "0")
    private String name;
    /**
     * 种类
     */
    @Excel(name = "照明灯具种类", orderNum = "1")
    private String type;
    /**
     * 数量
     */
    @Excel(name = "照明设备数量", orderNum = "2")
    private Integer number;
    /**
     * 额定功率
     */
    @Excel(name = "照明灯具额定功率(W)", orderNum = "3")
    private Double ratedPower;
    /**
     * 安装方式
     */
    @Excel(name = "照明灯具安装方式", orderNum = "4")
    private String installationMode;

    public boolean isNull() {
        return (this.name == null || "/".equals(this.name))
                && (this.type == null || "/".equals(this.type))
                && this.number == null
                && this.ratedPower == null
                && (this.installationMode == null || "/".equals(this.installationMode))
                ;
    }
}
