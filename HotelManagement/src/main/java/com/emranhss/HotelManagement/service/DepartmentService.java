package com.emranhss.HotelManagement.service;

import com.emranhss.HotelManagement.entity.Department;
import com.emranhss.HotelManagement.entity.PoliceStation;
import com.emranhss.HotelManagement.repository.DepatmentRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service

public class DepartmentService {
    @Autowired
    private DepatmentRepo depatmentRepo;

    public List<Department> getAll() {

        return depatmentRepo.findAll();
    }

    public Department saveOrUpdate (Department d){
        return depatmentRepo.save(d);
    }



    public Optional<Department> getById(long id) {
        return depatmentRepo.findById(id);
    }

    public  void  delete(long id){

        depatmentRepo.deleteById(id);
    }

}
