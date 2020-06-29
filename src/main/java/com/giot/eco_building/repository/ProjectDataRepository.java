package com.giot.eco_building.repository;

import com.giot.eco_building.entity.ProjectData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

/**
 * @Author: pyt
 * @Date: 2020/6/29 11:17
 * @Description:
 */
@Repository
public interface ProjectDataRepository extends JpaRepository<ProjectData, Long> {
    Optional<ProjectData> findByProjectIdAndActualDateAndTypeAndIsMonth(Long projectId, Date actualDate, Integer type, Boolean isMonth);
}
