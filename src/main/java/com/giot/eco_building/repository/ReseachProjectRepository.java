package com.giot.eco_building.repository;

import com.giot.eco_building.entity.Project;
import com.giot.eco_building.entity.ReseachProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-11-17
 * Time: 14:05
 */
@Repository
public interface ReseachProjectRepository extends JpaRepository<ReseachProject, Long> {
    @Query(nativeQuery = true, value = "SELECT * FROM research_project " +
            "WHERE del_status = :delStatus ORDER BY power_consumption_per_unit_area DESC LIMIT 10;")
    List<ReseachProject> findByDelStatusAndOrderByPowerConsumptionPerUnitAreaDescAndLimit10(@Param("delStatus") Boolean delStatus);


    Optional<ReseachProject> findBySerialNumber(String serialNumber);

    List<ReseachProject> findByDelStatus(Boolean delStatus);

    List<ReseachProject> findByNameLikeAndDelStatus(String name, Boolean delStatus);

    List<ReseachProject> findByNameLikeAndIsItSuggestedToTransformAndDelStatus(String name, Boolean isItSuggestedToTransform, Boolean delStatus);

}
