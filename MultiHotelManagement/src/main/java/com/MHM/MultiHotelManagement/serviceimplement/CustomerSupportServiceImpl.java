package com.MHM.MultiHotelManagement.serviceimplement;


import com.MHM.MultiHotelManagement.dto.mapper.CustomerSupportMapper;
import com.MHM.MultiHotelManagement.dto.request.CustomerSupportRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.CustomerSupportResponseDTO;
import com.MHM.MultiHotelManagement.entity.Customer;
import com.MHM.MultiHotelManagement.entity.CustomerSupport;
import com.MHM.MultiHotelManagement.entity.User;

import com.MHM.MultiHotelManagement.repository.CustomerRepository;
import com.MHM.MultiHotelManagement.repository.CustomerSupportRepository;
import com.MHM.MultiHotelManagement.repository.UserRepository;
import com.MHM.MultiHotelManagement.service.CustomerSupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerSupportServiceImpl implements CustomerSupportService {

    private final CustomerSupportRepository supportRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CustomerSupportResponseDTO createTicket(CustomerSupportRequestDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        User agent = null;
        if (dto.getAgentId() != null) {
            agent = userRepository.findById(dto.getAgentId())
                    .orElseThrow(() -> new RuntimeException("Agent not found"));
        }

        CustomerSupport ticket = new CustomerSupport();
        ticket.setSubject(dto.getSubject());
        ticket.setDescription(dto.getDescription());
        ticket.setStatus(dto.getStatus());
        ticket.setPriority(dto.getPriority());
        ticket.setCustomer(customer);
        ticket.setAgent(agent);

        CustomerSupport saved = supportRepository.save(ticket);
        return CustomerSupportMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public CustomerSupportResponseDTO updateTicket(Long id, CustomerSupportRequestDTO dto) {
        CustomerSupport ticket = supportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.setSubject(dto.getSubject());
        ticket.setDescription(dto.getDescription());
        ticket.setStatus(dto.getStatus());
        ticket.setPriority(dto.getPriority());

        if (dto.getAgentId() != null) {
            User agent = userRepository.findById(dto.getAgentId())
                    .orElseThrow(() -> new RuntimeException("Agent not found"));
            ticket.setAgent(agent);
        }

        CustomerSupport updated = supportRepository.save(ticket);
        return CustomerSupportMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void closeTicket(Long id) {
        CustomerSupport ticket = supportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticket.setStatus(com.MHM.MultiHotelManagement.enums.CustomerSupportTicketStatus.CLOSED);
        supportRepository.save(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerSupportResponseDTO> getTicketsByCustomer(Long customerId) {
        return supportRepository.findByCustomer_Id(customerId)
                .stream()
                .map(CustomerSupportMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerSupportResponseDTO> getTicketsByAgent(Long agentId) {
        return supportRepository.findByAgent_Id(agentId)
                .stream()
                .map(CustomerSupportMapper::toDTO)
                .collect(Collectors.toList());
    }
}
