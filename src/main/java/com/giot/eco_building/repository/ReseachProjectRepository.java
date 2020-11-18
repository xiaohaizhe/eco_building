package com.giot.eco_building.repository;

import com.giot.eco_building.entity.ReseachProject;
import org.springframework.data.jpa.repository.JpaRepository;
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
    Optional<ReseachProject> findBySerialNumber(String serialNumber);

    List<ReseachProject> findByDelStatus(Boolean delStatus);

    List<ReseachProject> findByNameLikeAndDelStatus(String name, Boolean delStatus);

    List<ReseachProject> findByNameLikeAndIsItSuggestedToTransformAndDelStatus(String name, Boolean isItSuggestedToTransform, Boolean delStatus);

}
