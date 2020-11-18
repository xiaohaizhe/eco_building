package com.giot.eco_building.repository;

import com.giot.eco_building.entity.WaterPump;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-11-18
 * Time: 11:30
 */
@Repository
public interface WaterPumpRepository extends JpaRepository<WaterPump, Long> {
    List<WaterPump> findBySerialNumber(String serialNumber);
}
