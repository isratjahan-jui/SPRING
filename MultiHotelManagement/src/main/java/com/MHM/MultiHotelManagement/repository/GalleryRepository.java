package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Long> {
}
