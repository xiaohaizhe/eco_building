package com.giot.eco_building.repository;

import com.giot.eco_building.entity.Action;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: pyt
 * @Date: 2020/6/16 15:03
 * @Description:
 */
@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {
}
