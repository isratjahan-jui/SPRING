package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.SupportReplyMapper;
import com.MHM.MultiHotelManagement.dto.request.NotificationRequestDTO;
import com.MHM.MultiHotelManagement.dto.request.SupportReplyRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.SupportReplyResponseDTO;
import com.MHM.MultiHotelManagement.entity.CustomerSupport;
import com.MHM.MultiHotelManagement.entity.SupportReply;
import com.MHM.MultiHotelManagement.entity.User;
import com.MHM.MultiHotelManagement.enums.CustomerSupportTicketStatus;
import com.MHM.MultiHotelManagement.enums.NotificationChannel;
import com.MHM.MultiHotelManagement.enums.NotificationType;
import com.MHM.MultiHotelManagement.enums.Role;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.CustomerSupportRepository;
import com.MHM.MultiHotelManagement.repository.SupportReplyRepository;
import com.MHM.MultiHotelManagement.repository.UserRepository;
import com.MHM.MultiHotelManagement.service.NotificationService;
import com.MHM.MultiHotelManagement.service.SupportReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportReplyServiceImpl implements SupportReplyService {

    private final SupportReplyRepository replyRepository;
    private final CustomerSupportRepository supportRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public SupportReplyResponseDTO addReply(SupportReplyRequestDTO dto) {
        CustomerSupport ticket = supportRepository.findById(dto.getTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

        User replier = userRepository.findById(dto.getReplierId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SupportReply reply = new SupportReply();
        reply.setMessage(dto.getMessage());
        reply.setTicket(ticket);
        reply.setReplier(replier);
        reply.setIsInternal(dto.getIsInternal() != null ? dto.getIsInternal() : false);

        SupportReply saved = replyRepository.save(reply);

        if (ticket.getStatus() == CustomerSupportTicketStatus.PENDING) {
            ticket.setStatus(CustomerSupportTicketStatus.IN_PROGRESS);
        }

        boolean isStaffReply = replier.getRole() == Role.ADMIN || replier.getRole() == Role.HOTEL_OWNER;
        if (isStaffReply && ticket.getFirstResponseAt() == null) {
            ticket.setFirstResponseAt(LocalDateTime.now());
        }

        supportRepository.save(ticket);

        try {
            if (isStaffReply) {
                NotificationRequestDTO customerNotif = new NotificationRequestDTO();
                customerNotif.setUserId(ticket.getCustomer().getUser().getId());
                customerNotif.setType(NotificationType.SUPPORT_REPLIED);
                customerNotif.setChannel(NotificationChannel.WEB);
                customerNotif.setMessage("Reply for ticket #" + ticket.getId() + " (" + ticket.getSubject() + "): " + dto.getMessage());
                notificationService.createNotification(customerNotif);
            } else {
                if (ticket.getAgent() != null) {
                    NotificationRequestDTO agentNotif = new NotificationRequestDTO();
                    agentNotif.setUserId(ticket.getAgent().getId());
                    agentNotif.setType(NotificationType.SUPPORT_REPLIED);
                    agentNotif.setChannel(NotificationChannel.WEB);
                    agentNotif.setMessage("Customer replied to ticket #" + ticket.getId() + ": " + dto.getMessage());
                    notificationService.createNotification(agentNotif);
                } else if (ticket.getHotel() != null && ticket.getHotel().getOwner() != null
                        && ticket.getHotel().getOwner().getUser() != null) {
                    NotificationRequestDTO ownerNotif = new NotificationRequestDTO();
                    ownerNotif.setUserId(ticket.getHotel().getOwner().getUser().getId());
                    ownerNotif.setType(NotificationType.SUPPORT_REPLIED);
                    ownerNotif.setChannel(NotificationChannel.WEB);
                    ownerNotif.setMessage("Customer replied to ticket #" + ticket.getId() + " for " + ticket.getHotel().getHotelName() + ": " + dto.getMessage());
                    notificationService.createNotification(ownerNotif);
                }
            }
        } catch (Exception ignored) {}

        return SupportReplyMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportReplyResponseDTO> getRepliesByTicket(Long ticketId) {
        return replyRepository.findByTicket_IdOrderByCreatedAtAsc(ticketId)
                .stream()
                .map(SupportReplyMapper::toDTO)
                .collect(Collectors.toList());
    }
}
