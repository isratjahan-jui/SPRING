package com.emranhss.HotelManagement.controller;

import com.emranhss.HotelManagement.entity.PoliceStation;
import com.emranhss.HotelManagement.service.PoliceStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policeStation/")
public class PoliceStationController {
    @Autowired
    private PoliceStationService policeStationService;


//    @PostMapping
//    public void save(@RequestBody PoliceStation p){
//        policeStationService.saveOrUpdate(p);
//    }
//
//    public List<PoliceStation> getAll(){
//        return policeStationService.getAll();
//    }


    @PostMapping
    public ResponseEntity<PoliceStation> save(@RequestBody PoliceStation p) {

        PoliceStation savedPoliceStation = policeStationService.saveOrUpdate(p);
        return new ResponseEntity<>(savedPoliceStation, HttpStatus.CREATED);
    }


    @GetMapping
    public List<PoliceStation> getAll() {
        return policeStationService.getAll();
    }


}
