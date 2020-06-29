package com.giot.eco_building.entity;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * @Author: pyt
 * @Date: 2020/6/15 14:56
 * @Description:
 */
@Entity
@Data
public class Action {
    @Id
    @GeneratedValue(generator = "SnowflakeId")
    @GenericGenerator(name = "SnowflakeId", strategy = "com.giot.eco_building.utils.SnowflakeId")
    private Long id;
    /**
     * 方法描述
     */
    @Column(name = "action_desc")
    private String actionDesc;
    /**
     * 操作类型
     */
    private Integer type;
    /**
     * 操作ip
     */
    @Column(name = "action_ip")
    private String actionIp;
    /**
     * 操作用户
     */
    @Column(name = "user_id")
    private Long userId;
    /**
     * 操作时间
     */
    @CreatedDate
    @Column(updatable = false, nullable = false, name = "action_time")
    private Date actionTime;
}
