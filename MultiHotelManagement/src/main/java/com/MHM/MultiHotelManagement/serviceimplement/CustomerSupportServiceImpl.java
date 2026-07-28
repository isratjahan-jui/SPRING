package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.CustomerSupportMapper;
import com.MHM.MultiHotelManagement.dto.request.CustomerSupportRequestDTO;
import com.MHM.MultiHotelManagement.dto.request.EscalationRequestDTO;
import com.MHM.MultiHotelManagement.dto.request.NotificationRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.CustomerSupportResponseDTO;
import com.MHM.MultiHotelManagement.dto.response.SupportStatsResponseDTO;
import com.MHM.MultiHotelManagement.entity.*;
import com.MHM.MultiHotelManagement.enums.*;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.*;
import com.MHM.MultiHotelManagement.service.CustomerSupportService;
import com.MHM.MultiHotelManagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerSupportServiceImpl implements CustomerSupportService {

    private final CustomerSupportRepository supportRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final HotelRepository hotelRepository;
    private final HotelOwnerRepository hotelOwnerRepository;
    private final NotificationService notificationService;

    private static final Set<String> HOTEL_CATEGORIES = Set.of(
            "HOTEL_SERVICE", "ROOM", "STAFF", "BOOKING", "CANCELATION"
    );

    private static final Set<String> SYSTEM_CATEGORIES = Set.of(
            "PAYMENT", "REFUND", "BILLING", "OTHER"
    );

    private static final Set<String> URGENT_KEYWORDS = Set.of(
            "urgent", "emergency", "immediately", "asap", "critical",
            "life threatening", "danger", "hazard", "safety", "fire", "flood"
    );

    @Override
    @Transactional
    public CustomerSupportResponseDTO createTicket(CustomerSupportRequestDTO dto) {
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Hotel hotel = null;
        if (dto.getHotelId() != null) {
            hotel = hotelRepository.findById(dto.getHotelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        }

        CustomerSupport ticket = new CustomerSupport();
        ticket.setSubject(dto.getSubject());
        ticket.setDescription(dto.getDescription());
        ticket.setStatus(CustomerSupportTicketStatus.PENDING);

        CustomerSupportTicketPriority priority = dto.getPriority() != null ? dto.getPriority() : CustomerSupportTicketPriority.MEDIUM;
        String textToCheck = ((dto.getSubject() != null ? dto.getSubject() : "") + " " + (dto.getDescription() != null ? dto.getDescription() : "")).toLowerCase();
        if (priority != CustomerSupportTicketPriority.URGENT) {
            for (String keyword : URGENT_KEYWORDS) {
                if (textToCheck.contains(keyword)) {
                    priority = CustomerSupportTicketPriority.URGENT;
                    break;
                }
            }
        }
        ticket.setPriority(priority);
        ticket.setCategory(dto.getCategory());
        ticket.setCustomer(customer);
        ticket.setHotel(hotel);
        ticket.setEscalated(false);

        if (dto.getAgentId() != null) {
            User agent = userRepository.findById(dto.getAgentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));
            ticket.setAgent(agent);
            ticket.setStatus(CustomerSupportTicketStatus.IN_PROGRESS);
        }

        CustomerSupport saved = supportRepository.save(ticket);

        if (hotel != null && hotel.getOwner() != null && hotel.getOwner().getUser() != null) {
            try {
                NotificationRequestDTO notification = new NotificationRequestDTO();
                notification.setUserId(hotel.getOwner().getUser().getId());
                notification.setType(NotificationType.SUPPORT_REPLIED);
                notification.setChannel(NotificationChannel.WEB);
                notification.setMessage("New support ticket #" + saved.getId() + " for " + hotel.getHotelName() + ": " + saved.getSubject());
                notificationService.createNotification(notification);
            } catch (Exception ignored) {}
        }

        return CustomerSupportMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public CustomerSupportResponseDTO updateTicket(Long id, CustomerSupportRequestDTO dto) {
        CustomerSupport ticket = supportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        if (dto.getSubject() != null) ticket.setSubject(dto.getSubject());
        if (dto.getDescription() != null) ticket.setDescription(dto.getDescription());
        if (dto.getStatus() != null) {
            ticket.setStatus(dto.getStatus());
            if (dto.getStatus() == CustomerSupportTicketStatus.RESOLVED && ticket.getResolvedAt() == null) {
                ticket.setResolvedAt(LocalDateTime.now());
            }
        }
        if (dto.getPriority() != null) ticket.setPriority(dto.getPriority());
        if (dto.getCategory() != null) ticket.setCategory(dto.getCategory());

        if (dto.getAgentId() != null) {
            User agent = userRepository.findById(dto.getAgentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));
            ticket.setAgent(agent);
            if (ticket.getStatus() == CustomerSupportTicketStatus.PENDING) {
                ticket.setStatus(CustomerSupportTicketStatus.IN_PROGRESS);
            }
        }

        if (dto.getHotelId() != null) {
            Hotel hotel = hotelRepository.findById(dto.getHotelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
            ticket.setHotel(hotel);
        }

        CustomerSupport updated = supportRepository.save(ticket);
        return CustomerSupportMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void closeTicket(Long id) {
        CustomerSupport ticket = supportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        ticket.setStatus(CustomerSupportTicketStatus.CLOSED);
        if (ticket.getResolvedAt() == null) {
            ticket.setResolvedAt(LocalDateTime.now());
        }
        supportRepository.save(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerSupportResponseDTO getTicketById(Long id) {
        CustomerSupport ticket = supportRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        return CustomerSupportMapper.toDTO(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerSupportResponseDTO> getAllTickets() {
        return supportRepository.findAllWithDetails()
                .stream()
                .map(CustomerSupportMapper::toDTO)
                .collect(Collectors.toList());
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

    @Override
    @Transactional(readOnly = true)
    public List<CustomerSupportResponseDTO> getTicketsByHotel(Long hotelId) {
        return supportRepository.findByHotel_Id(hotelId)
                .stream()
                .map(CustomerSupportMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerSupportResponseDTO assignAgent(Long ticketId, Long agentId) {
        CustomerSupport ticket = supportRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        User agent = userRepository.findById(agentId)
                .orElseThrow(() -> new ResourceNotFoundException("Agent not found"));

        ticket.setAgent(agent);
        if (ticket.getStatus() == CustomerSupportTicketStatus.PENDING) {
            ticket.setStatus(CustomerSupportTicketStatus.IN_PROGRESS);
        }

        CustomerSupport updated = supportRepository.save(ticket);

        try {
            NotificationRequestDTO notification = new NotificationRequestDTO();
            notification.setUserId(ticket.getCustomer().getUser().getId());
            notification.setType(NotificationType.SUPPORT_REPLIED);
            notification.setChannel(NotificationChannel.WEB);
            notification.setMessage("Your support ticket #" + ticket.getId() + " has been assigned to " + agent.getName() + ".");
            notificationService.createNotification(notification);
        } catch (Exception ignored) {}

        return CustomerSupportMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public CustomerSupportResponseDTO escalateTicket(Long ticketId, EscalationRequestDTO dto) {
        CustomerSupport ticket = supportRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        ticket.setEscalated(true);
        ticket.setStatus(CustomerSupportTicketStatus.ESCALATED);
        ticket.setPriority(CustomerSupportTicketPriority.HIGH);

        if (ticket.getAgent() == null) {
            userRepository.findByRole(Role.ADMIN).stream().findFirst().ifPresent(ticket::setAgent);
        }

        CustomerSupport updated = supportRepository.save(ticket);

        try {
            List<User> admins = userRepository.findByRole(Role.ADMIN);
            for (User admin : admins) {
                NotificationRequestDTO notification = new NotificationRequestDTO();
                notification.setUserId(admin.getId());
                notification.setType(NotificationType.SUPPORT_REPLIED);
                notification.setChannel(NotificationChannel.WEB);
                String reason = (dto.getReason() != null && !dto.getReason().isBlank())
                        ? " Reason: " + dto.getReason()
                        : "";
                notification.setMessage("Ticket #" + ticket.getId() + " has been escalated to Admin." + reason);
                notificationService.createNotification(notification);
            }

            NotificationRequestDTO customerNotif = new NotificationRequestDTO();
            customerNotif.setUserId(ticket.getCustomer().getUser().getId());
            customerNotif.setType(NotificationType.SUPPORT_REPLIED);
            customerNotif.setChannel(NotificationChannel.WEB);
            customerNotif.setMessage("Your ticket #" + ticket.getId() + " has been escalated to our admin team for priority resolution.");
            notificationService.createNotification(customerNotif);
        } catch (Exception ignored) {}

        return CustomerSupportMapper.toDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public SupportStatsResponseDTO getStats() {
        List<CustomerSupport> all = supportRepository.findAllWithDetails();
        return computeStats(all);
    }

    @Override
    @Transactional(readOnly = true)
    public SupportStatsResponseDTO getStatsByHotel(Long hotelId) {
        List<CustomerSupport> tickets = supportRepository.findByHotel_Id(hotelId);
        return computeStats(tickets);
    }

    private SupportStatsResponseDTO computeStats(List<CustomerSupport> tickets) {
        SupportStatsResponseDTO stats = new SupportStatsResponseDTO();
        stats.setTotalTickets(tickets.size());
        stats.setPendingCount(tickets.stream().filter(t -> t.getStatus() == CustomerSupportTicketStatus.PENDING).count());
        stats.setInProgressCount(tickets.stream().filter(t -> t.getStatus() == CustomerSupportTicketStatus.IN_PROGRESS).count());
        stats.setEscalatedCount(tickets.stream().filter(t -> t.getStatus() == CustomerSupportTicketStatus.ESCALATED).count());
        stats.setResolvedCount(tickets.stream().filter(t -> t.getStatus() == CustomerSupportTicketStatus.RESOLVED).count());
        stats.setClosedCount(tickets.stream().filter(t -> t.getStatus() == CustomerSupportTicketStatus.CLOSED).count());
        stats.setUnassignedCount(tickets.stream().filter(t -> t.getAgent() == null).count());

        double avgResponse = tickets.stream()
                .filter(t -> t.getFirstResponseAt() != null && t.getCreatedAt() != null)
                .mapToLong(t -> Duration.between(t.getCreatedAt(), t.getFirstResponseAt()).toMinutes())
                .average().orElse(0.0);
        stats.setAvgResponseTimeHours(Math.round(avgResponse / 60.0 * 10.0) / 10.0);

        double avgResolution = tickets.stream()
                .filter(t -> t.getResolvedAt() != null && t.getCreatedAt() != null)
                .mapToLong(t -> Duration.between(t.getCreatedAt(), t.getResolvedAt()).toMinutes())
                .average().orElse(0.0);
        stats.setAvgResolutionTimeHours(Math.round(avgResolution / 60.0 * 10.0) / 10.0);

        return stats;
    }
}
