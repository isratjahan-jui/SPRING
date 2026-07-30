package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.LoginAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    Optional<LoginAttempt> findByEmailAndSuccessFalse(String email);

    Optional<LoginAttempt> findByEmail(String email);

    void deleteByCreatedAtBefore(LocalDateTime before);
}