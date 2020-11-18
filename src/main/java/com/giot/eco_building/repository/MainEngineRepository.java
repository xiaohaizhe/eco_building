package com.giot.eco_building.repository;

import com.giot.eco_building.entity.MainEngine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-11-18
 * Time: 11:35
 */
@Repository
public interface MainEngineRepository extends JpaRepository<MainEngine, Long> {
    List<MainEngine> findBySerialNumber(String serialNumber);
}
