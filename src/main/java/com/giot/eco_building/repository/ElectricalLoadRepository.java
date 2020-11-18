package com.giot.eco_building.repository;

import com.giot.eco_building.entity.ElectricalLoad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-11-18
 * Time: 11:38
 */
@Repository
public interface ElectricalLoadRepository extends JpaRepository<ElectricalLoad, Long> {
    List<ElectricalLoad> findBySerialNumber(String serialNumber);
}
