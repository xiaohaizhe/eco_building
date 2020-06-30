package com.giot.eco_building.repository;

import com.giot.eco_building.entity.Action;
import com.giot.eco_building.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @Author: pyt
 * @Date: 2020/6/16 15:03
 * @Description:
 */
@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {
    Page<Action> findAllByTypeInAndActionTimeBetween(Integer[] types, Date start, Date end, Pageable pageable);

    Page<Action> findAllByTypeIn(Integer[] types, Pageable pageable);
}
