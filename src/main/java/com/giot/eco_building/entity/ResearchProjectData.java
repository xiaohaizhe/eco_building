package com.giot.eco_building.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-11-18
 * Time: 15:18
 */
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class ResearchProjectData implements Serializable {
    private static final long serialVersionUID = -7042816597359010218L;
    @Id
    @GeneratedValue(generator = "SnowflakeId")
    @GenericGenerator(name = "SnowflakeId", strategy = "com.giot.eco_building.utils.SnowflakeId")
    private Long id;
    private Long reseachProjectId;
    private String serialNumber;
    /**
     * 实际时间
     */
//    @JsonFormat(pattern = "yyyy-MM", timezone = "GMT+8")
    @Column(name = "actual_date")
    private Date actualDate;
    /**
     * 数据类型：水-0电-1气-2热-3
     */
    private Integer type;
    private Double value;
}
