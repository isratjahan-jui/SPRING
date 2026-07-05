package com.emranhss.HotelManagement.repository;

import com.emranhss.HotelManagement.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepatmentRepo extends JpaRepository<Department, Long> {

}
