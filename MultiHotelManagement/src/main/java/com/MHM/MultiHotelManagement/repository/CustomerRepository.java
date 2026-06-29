package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import com.MHM.MultiHotelManagement.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findCustomerByUser_Id(Long userId);
    Boolean existsByUser_Id(Long userId);

//    Customer findCustomerByUser_Id(Integer id);

    @Query("""
        SELECT c FROM Customer c
        LEFT JOIN FETCH c.user
        WHERE c.id = :id
    """)
    Optional<Customer> findByIdWithDetails(@Param("id") Long id);


    @Query("""
        SELECT c FROM Customer c
        LEFT JOIN FETCH c.user
    """)
    List<Customer> findAllWithDetails();



    @Query("select c from Customer c where c.user.email = : email")
    Optional<Customer> findByUserEmail(@Param("email") String email);

    Optional<Customer> findByEmail(String email);







//    // নির্দিষ্ট Customer এর সাথে User entity একসাথে লোড করা
//    @Query("""
//        SELECT c FROM Customer c
//        LEFT JOIN FETCH c.user
//        WHERE c.id = :id
//    """)
//    Optional<Customer> findByIdWithUser(@Param("id") Long id);
//
//    // সব Customer এর সাথে User entity একসাথে লোড করা
//    @Query("""
//        SELECT c FROM Customer c
//        LEFT JOIN FETCH c.user
//    """)
//    List<Customer> findAllWithUser();



}
