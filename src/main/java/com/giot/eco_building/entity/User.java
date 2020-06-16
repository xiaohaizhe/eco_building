package com.giot.eco_building.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author: pyt
 * @Date: 2020/6/9 9:29
 * @Description:
 */
@Entity
@Table
@Data
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 权限
     */
    private String authority;
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
    @Column(nullable = false)
    private Date lastModified;
    /**
     * 删除标记位：0-有效，1-无效
     */
    @Column(nullable = false, columnDefinition = "bit default 0")
    private Boolean delStatus;
}