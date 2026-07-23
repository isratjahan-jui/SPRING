package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.User;
import com.MHM.MultiHotelManagement.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    Boolean existsByPhone(String phone);
    Optional<User> findByVerificationToken(String token);
    List<User> findByRole(Role role);
}
