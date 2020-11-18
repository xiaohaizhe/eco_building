package com.giot.eco_building.repository;

import com.giot.eco_building.entity.TerminalEquipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-11-18
 * Time: 11:32
 */
@Repository
public interface TerminalEquipmentRepository extends JpaRepository<TerminalEquipment, Long> {
    List<TerminalEquipment> findBySerialNumber(String serialNumber);
}
