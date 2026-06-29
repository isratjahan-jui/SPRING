package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.CustomerRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.CustomerResponseDTO;
import com.MHM.MultiHotelManagement.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tools.jackson.databind.ObjectMapper;


import java.util.List;

@RestController
@RequestMapping("/api/customers/")
public class CustomerController {

    @Autowired
    private CustomerService customerService;


    @PostMapping
    public ResponseEntity<CustomerResponseDTO> create(
            @RequestPart("customer") String customerJson,
            @RequestPart(value = "image", required = false) MultipartFile image) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        CustomerRequestDTO dto = mapper.readValue(customerJson, CustomerRequestDTO.class);

        return new ResponseEntity<>(
                customerService.saveCustomer(dto, image),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAll() {
        List<CustomerResponseDTO> list = customerService.getAllCustomers();
        return list.isEmpty()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public CustomerResponseDTO getById(@PathVariable Long id) {
        return customerService.getCustomerById(id);
    }

    @PutMapping("/{id}")
    public CustomerResponseDTO update(
            @PathVariable Long id,
            @RequestPart("customer") CustomerRequestDTO dto,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return customerService.updateCustomer(id, dto, image);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok("Deleted successfully");
    }

}



