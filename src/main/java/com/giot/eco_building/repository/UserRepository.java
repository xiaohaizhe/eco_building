package com.giot.eco_building.repository;

import com.giot.eco_building.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author: pyt
 * @Date: 2020/6/9 9:48
 * @Description:
 */
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByUsername(String username);
}
