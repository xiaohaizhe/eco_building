package com.giot.eco_building.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.giot.eco_building.utils.validation.LocationValidation;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlSchemaType;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: pyt
 * @Date: 2020/6/10 14:35
 * @Description:
 */
@Entity
@Table
@Data
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Project implements Serializable {
    private static final long serialVersionUID = -6036280645685673399L;

    @Id
    @GeneratedValue(generator = "SnowflakeId")
    @GenericGenerator(name = "SnowflakeId", strategy = "com.giot.eco_building.utils.SnowflakeId")
    private Long id;
    /**
     * 建筑名称
     */
    private String name;
    /**
     * 建筑地址
     */
    private String address;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 区
     */
    private String district;
    /**
     * 街道
     */
    private String street;
    /**
     * 纬度
     */
    @LocationValidation(min = 3.86f, max = 53.55f)
    private Float latitude;
    /**
     * 经度
     */
    @LocationValidation(min = 73.66f, max = 135.06f)
    private Float longitude;
    /**
     * 建筑类型
     */
    private String type;
    /**
     * 建筑面积
     */
    private Double area;
    /**
     * 建成时间
     */
    @Column(name = "built_time")
    private Date builtTime;
    /**
     * 楼层
     */
    @Column(name = "number_of_floors")
    private Integer numberOfFloors;
    /**
     * 项目图片
     */
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @JsonIgnore
    @XmlSchemaType(name = "base64Binary")
    private Byte[] photo;
    /**
     * 创建时间
     */
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private Date created;
    /**
     * 最新修改时间
     */
    @LastModifiedDate
    @Column(nullable = false, name = "last_modified")
    private Date lastModified;
    /**
     * 删除标记位：0-有效，1-无效
     */
    @Column(nullable = false, columnDefinition = "bit default 0", name = "del_status")
    private Boolean delStatus;

    public String getAddress() {
        return this.province + this.city + this.district + this.street;
    }


}
