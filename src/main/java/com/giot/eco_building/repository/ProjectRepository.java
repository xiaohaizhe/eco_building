package com.giot.eco_building.repository;

import com.giot.eco_building.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: pyt
 * @Date: 2020/6/11 9:25
 * @Description:
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {
    Project findByName(String name);

    @Query(value = "SELECT DISTINCT province FROM Project where province != \"\"", nativeQuery = true)
    List<String> findDistinctProvince();

    @Query(value = "SELECT DISTINCT city FROM Project where province = :province and city != \"\"", nativeQuery = true)
    List<String> findDistinctCityByProvince(String province);

    @Query(value = "SELECT DISTINCT district FROM Project where city = :city and district != \"\"", nativeQuery = true)
    List<String> findDistinctDistrictByCity(String city);

    @Query(value = "SELECT DISTINCT street FROM Project where district = :district and street != \"\"", nativeQuery = true)
    List<String> findDistinctStreetByDistrict(String district);
}
