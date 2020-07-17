package com.giot.eco_building.repository;

import com.giot.eco_building.entity.Project;
import com.giot.eco_building.model.CityCount;
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
    Project findByNameAndDelStatus(String name, Boolean delStatus);

    @Query(nativeQuery = true, value = "SELECT * FROM project " +
            "WHERE province = :province and del_status = :delStatus ORDER BY power_consumption_per_unit_area DESC LIMIT 10;")
    List<Project> findByProvinceAndDelStatusAndOrderByPowerConsumptionPerUnitAreaDescAndLimit10(String province, Boolean delStatus);

    @Query(nativeQuery = true,
            value = "SELECT city,COUNT(city) as count FROM `project` where province = :province and city != '' and del_status = :delStatus GROUP BY city ORDER BY count desc LIMIT 5")
    List<Object[]> findCityCountByProvinceAndDelStatus(String province, Boolean delStatus);

    @Query(value = "SELECT DISTINCT province FROM Project where province != \"\"", nativeQuery = true)
    List<String> findDistinctProvince();

    @Query(value = "SELECT DISTINCT city FROM Project where province = :province and city != \"\"", nativeQuery = true)
    List<String> findDistinctCityByProvince(String province);

    @Query(value = "SELECT DISTINCT district FROM Project where city = :city and district != \"\"", nativeQuery = true)
    List<String> findDistinctDistrictByCity(String city);

    @Query(value = "SELECT DISTINCT street FROM Project where district = :district and street != \"\"", nativeQuery = true)
    List<String> findDistinctStreetByDistrict(String district);
}
