package com.MHM.MultiHotelManagement.dto.mapper;
import com.MHM.MultiHotelManagement.dto.response.CustomerResponseDTO;
import com.MHM.MultiHotelManagement.entity.Customer;
public class CustomerMapperDTO {

    // Entity → ResponseDTO
    public static CustomerResponseDTO toDTO(Customer customer) {
        CustomerResponseDTO dto = new CustomerResponseDTO();

        dto.setId(customer.getId());

        // Flatten User fields (auth account)
        if (customer.getUser() != null) {
            dto.setUserId(customer.getUser().getId());
            dto.setName(customer.getUser().getName());
            dto.setEmail(customer.getUser().getEmail());
            dto.setPhone(customer.getUser().getPhone());
            dto.setRole(
                    customer.getUser().getRole() != null
                            ? customer.getUser().getRole().name()
                            : null
            );
        }

        // Customer profile fields
        dto.setCustomerName(customer.getCustomerName());
        dto.setAddress(customer.getAddress());
        dto.setGender(customer.getGender());
        dto.setDateOfBirth(
                customer.getDateOfBirth() != null
                        ? customer.getDateOfBirth().toString()
                        : null
        );
        dto.setImage(customer.getImage());

        return dto;
    }


}
