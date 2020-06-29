package com.giot.eco_building.repository;

import com.giot.eco_building.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: pyt
 * @Date: 2020/6/11 9:25
 * @Description:
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Project findByName(String name);
}
