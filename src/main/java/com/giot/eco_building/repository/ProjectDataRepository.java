package com.giot.eco_building.repository;

import com.giot.eco_building.entity.ProjectData;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @Author: pyt
 * @Date: 2020/6/29 11:17
 * @Description:
 */
@Repository
public interface ProjectDataRepository extends JpaRepository<ProjectData, Long> {
    @Query(nativeQuery = true, value = "select * from project_data where project_id = ?1 and type = ?2 and is_month= ?3 and actual_date >= ?4 and actual_date <= ?5 and del_status = false order by actual_date asc ")
    List<ProjectData> findByProjectIdAndIsMonthAndTypeAndActualDateBetween(Long projectId, Integer type, Boolean isMonth, Date startActualDate, Date endActualDate);

    Optional<ProjectData> findByProjectIdAndActualDateAndTypeAndIsMonth(Long projectId, Date actualDate, Integer type, Boolean isMonth);

    ProjectData findTopByProjectIdAndIsMonthAndTypeAndValueGreaterThanOrderByActualDateDesc(Long projectId, Boolean isMonth, Integer type, Double value);
}
