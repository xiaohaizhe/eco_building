package com.giot.eco_building.repository;

import com.giot.eco_building.entity.ProjectData;
import com.giot.eco_building.entity.ResearchProjectData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-11-18
 * Time: 15:29
 */
@Repository
public interface ResearchProjectDataRepository extends JpaRepository<ResearchProjectData, Long> {
    @Query(nativeQuery = true, value = "select * from ResearchProjectData where reseachProjectId = ?1 and type = ?2 and actual_date >= ?3 and actual_date <= ?4  order by actual_date asc ")
    List<ResearchProjectData> findByProjectIdAndIsMonthAndTypeAndActualDateBetween(Long projectId, Integer type, Date startActualDate, Date endActualDate);


    List<ResearchProjectData> findBySerialNumberAndTypeAndActualDate(String serialNumber, Integer type, Date actualDate);

    List<ResearchProjectData> findByReseachProjectIdAndTypeAndActualDateBetweenOrderByActualDate(Long reseachProjectId, Integer type, Date actualDateStart, Date actualDateEnd);
}
