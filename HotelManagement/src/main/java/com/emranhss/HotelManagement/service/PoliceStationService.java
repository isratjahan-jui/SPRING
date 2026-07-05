package com.emranhss.HotelManagement.service;

import com.emranhss.HotelManagement.entity.PoliceStation;
import com.emranhss.HotelManagement.repository.PoliceStationRepsitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;


    @Service
    public class PoliceStationService {

        @Autowired
        private PoliceStationRepsitory stationRepsitory;

        public List<PoliceStation> getAll() {

            return stationRepsitory.findAll();
        }

        public PoliceStation saveOrUpdate(PoliceStation p) {

            return   stationRepsitory.save(p);
        }

        public Optional<PoliceStation> getById(long id) {
            return stationRepsitory.findById(id);
        }

        public  void  delete(long id){

            stationRepsitory.deleteById(id);
        }


    }
