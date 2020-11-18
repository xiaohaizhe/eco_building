package com.giot.eco_building.repository;

import com.giot.eco_building.entity.AirConditionBox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-11-18
 * Time: 11:43
 */
@Repository
public interface AirConditionBoxRepository extends JpaRepository<AirConditionBox, Long> {
    List<AirConditionBox> findBySerialNumber(String serialNumber);
}
