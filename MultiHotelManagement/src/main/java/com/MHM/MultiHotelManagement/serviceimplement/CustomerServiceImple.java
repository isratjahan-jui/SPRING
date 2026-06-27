package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.CustomerMapperDTO;
import com.MHM.MultiHotelManagement.dto.request.CustomerRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.CustomerResponseDTO;
import com.MHM.MultiHotelManagement.entity.Customer;
import com.MHM.MultiHotelManagement.repository.CustomerRepository;
import com.MHM.MultiHotelManagement.service.CustomerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImple implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImple(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponseDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
        return CustomerMapperDTO.toResponseDTO(customer);
    }

    @Override
    public CustomerResponseDTO getCustomerByUserId(Long userId) {
        Customer customer = customerRepository.findCustomerByUser_Id(userId.intValue());
        if (customer == null) {
            throw new EntityNotFoundException("Customer not found for user ID: " + userId);
        }
        return CustomerMapperDTO.toResponseDTO(customer);
    }

    @Override
    public Optional<CustomerResponseDTO> getCustomerByEmail(String email) {
        return customerRepository.findByUserEmail(email).map(CustomerMapperDTO::toResponseDTO);
    }

    @Override
    public List<CustomerResponseDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream().map(CustomerMapperDTO::toResponseDTO).toList();
    }

    @Override
    public CustomerResponseDTO saveCustomer(CustomerRequestDTO customerRequestDTO) {
        Customer customer = CustomerMapperDTO.toEntity(customerRequestDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return CustomerMapperDTO.toResponseDTO(savedCustomer);
    }

    @Override
    public CustomerResponseDTO updateCustomer(Long id, CustomerRequestDTO customerRequestDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));

        // Update fields
        customer.setName(customerRequestDTO.getName());
        customer.setEmail(customerRequestDTO.getEmail());
        customer.setPhone(customerRequestDTO.getPhone());
        customer.setAddress(customerRequestDTO.getAddress());
        customer.setGender(customerRequestDTO.getGender());
        customer.setDateOfBirth(customerRequestDTO.getDateOfBirth());
        customer.setImage(customerRequestDTO.getImage());

        Customer updatedCustomer = customerRepository.save(customer);
        return CustomerMapperDTO.toResponseDTO(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new EntityNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }
}
