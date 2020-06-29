package com.giot.eco_building.repository;

import com.giot.eco_building.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @Author: pyt
 * @Date: 2020/6/9 9:48
 * @Description:
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsernameAndDelStatus(String username, Boolean delStatus);

    Optional<User> findByIdAndDelStatus(Long id, Boolean delStatus);

    Page<User> findAllByAuthorityAndDelStatus(String authority, Boolean delStatus, Pageable pageable);
}
