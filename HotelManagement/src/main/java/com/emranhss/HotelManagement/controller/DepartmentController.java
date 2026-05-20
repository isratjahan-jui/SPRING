package com.emranhss.HotelManagement.controller;

import com.emranhss.HotelManagement.entity.Department;
import com.emranhss.HotelManagement.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping ("/api/department/")


public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping("")
    public ResponseEntity<Department> save(@RequestBody Department d) {

        Department s = departmentService.saveOrUpdate(d);
        return new ResponseEntity<>(s, HttpStatus.CREATED);
    }


    @GetMapping("")
    public List<Department> getAll() {
        return departmentService.getAll();
    }




}
