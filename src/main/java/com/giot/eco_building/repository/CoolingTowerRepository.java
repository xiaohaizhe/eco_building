package com.giot.eco_building.repository;

import com.giot.eco_building.entity.CoolingTower;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-11-18
 * Time: 11:40
 */
@Repository
public interface CoolingTowerRepository extends JpaRepository<CoolingTower, Long> {
    List<CoolingTower> findBySerialNumber(String serialNumber);
}
