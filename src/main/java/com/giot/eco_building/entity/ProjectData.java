package com.giot.eco_building.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: pyt
 * @Date: 2020/6/18 10:45
 * @Description:
 */
@Entity
@Data
@Table(name = "project_data")
@EntityListeners(AuditingEntityListener.class)
public class ProjectData implements Serializable {
    private static final long serialVersionUID = 6351140047131544583L;
    @Id
    @GeneratedValue(generator = "SnowflakeId")
    @GenericGenerator(name = "SnowflakeId", strategy = "com.giot.eco_building.utils.SnowflakeId")
    private Long id;
    /**
     * 所属项目
     */
    @Column(name = "project_id")
    private Long projectId;
    @Column(name = "serial_number")
    private String serialNumber;
    /**
     * 年数据/月数据
     * 0-年数据
     * 1-月数据
     */
    @JSONField(serialize = false)
    @Column(nullable = false, columnDefinition = "bit default 0", name = "is_month")
    private Boolean isMonth;
    /**
     * 实际时间
     */
//    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    @Column(name = "actual_date")
    private Date actualDate;
    /**
     * 数据类型：水电气
     */
    private Integer type;
    private Double value;
    /*@Column(name = "annual_value")
    private BigDecimal annualValue;
    @Column(name = "jan_value")
    private BigDecimal janValue;
    @Column(name = "feb_value")
    private BigDecimal febValue;
    @Column(name = "mar_value")
    private BigDecimal marValue;
    @Column(name = "apr_value")
    private BigDecimal aprValue;
    @Column(name = "may_value")
    private BigDecimal mayValue;
    @Column(name = "jun_value")
    private BigDecimal junValue;
    @Column(name = "jul_value")
    private BigDecimal julValue;
    @Column(name = "aug_value")
    private BigDecimal augValue;
    @Column(name = "sept_value")
    private BigDecimal septValue;
    @Column(name = "oct_value")
    private BigDecimal octValue;
    @Column(name = "nov_value")
    private BigDecimal novValue;
    @Column(name = "dec_value")
    private BigDecimal decValue;*/
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
    @JSONField(serialize = false)
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
