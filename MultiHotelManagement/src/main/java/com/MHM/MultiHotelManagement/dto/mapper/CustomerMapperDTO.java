package com.MHM.MultiHotelManagement.dto.mapper;


import com.MHM.MultiHotelManagement.dto.request.CustomerRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.CustomerResponseDTO;
import com.MHM.MultiHotelManagement.entity.Customer;

public class CustomerMapperDTO {

    // RequestDTO → Entity
    public static Customer toEntity(CustomerRequestDTO dto) {
        Customer customer = new Customer();
        customer.setCustomerName(dto.getName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setGender(dto.getGender());
        customer.setDateOfBirth(dto.getDateOfBirth());
        customer.setImage(dto.getImage());
        return customer;
    }

    // Entity → ResponseDTO
    public static CustomerResponseDTO toResponseDTO(Customer customer) {
        return new CustomerResponseDTO(
                customer.getId(),
                customer.getCustomerName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getGender(),
                customer.getDateOfBirth(),
                customer.getImage()
        );
    }
}
