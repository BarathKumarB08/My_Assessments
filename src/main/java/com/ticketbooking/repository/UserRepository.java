package com.ticketbooking.repository;

import com.ticketbooking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Modifying
    @Transactional
    @Query(name = "delete from tbl_users where email_id = :emailId", nativeQuery = true)
    Integer deleteUserByEmailId(@Param("emailId") String emailId);

    boolean existsByEmailId(String emailId);
}
