package com.fixflow.ai.service;

import com.fixflow.ai.dto.ticket.*;
import com.fixflow.ai.exception.BadRequestException;
import com.fixflow.ai.exception.ForbiddenException;
import com.fixflow.ai.exception.ResourceNotFoundException;
import com.fixflow.ai.model.Ticket;
import com.fixflow.ai.model.TicketStatusHistory;
import com.fixflow.ai.model.User;
import com.fixflow.ai.model.enums.Role;
import com.fixflow.ai.model.enums.TicketStatus;
import com.fixflow.ai.repository.TicketRepository;
import com.fixflow.ai.repository.TicketStatusHistoryRepository;
import com.fixflow.ai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketStatusHistoryRepository historyRepository;
    private final UserRepository userRepository;

    @Transactional
    public TicketDto create(TicketCreateRequest req, String requesterEmail) {
        User creator = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Ticket ticket = Ticket.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .category(req.getCategory())
                .priority(req.getPriority())
                .status(TicketStatus.OPEN)
                .creator(creator)
                .statusHistory(new ArrayList<>())
                .build();
        ticket = ticketRepository.save(ticket);
        // record initial status history
        TicketStatusHistory hist = TicketStatusHistory.builder()
                .ticket(ticket)
                .fromStatus(TicketStatus.OPEN)
                .toStatus(TicketStatus.OPEN)
                .changedBy(creator)
                .changedAt(Instant.now())
                .build();
        historyRepository.save(hist);
        ticket.getStatusHistory().add(hist);
        return toDto(ticket);
    }

    public List<TicketDto> list(String requesterEmail, String q, String status, String category, String priority) {
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Ticket> tickets;
        if (q != null && !q.isBlank()) {
            tickets = ticketRepository.search(q);
        } else if (status != null && !status.isBlank()) {
            try {
                tickets = ticketRepository.findByStatus(TicketStatus.valueOf(status.toUpperCase()));
            } catch (Exception e) {
                throw new BadRequestException("Invalid status");
            }
        } else if (category != null && !category.isBlank()) {
            try {
                tickets = ticketRepository.findByCategory(com.fixflow.ai.model.enums.TicketCategory.valueOf(category.toUpperCase()));
            } catch (Exception e) {
                throw new BadRequestException("Invalid category");
            }
        } else if (priority != null && !priority.isBlank()) {
            try {
                tickets = ticketRepository.findByPriority(com.fixflow.ai.model.enums.TicketPriority.valueOf(priority.toUpperCase()));
            } catch (Exception e) {
                throw new BadRequestException("Invalid priority");
            }
        } else {
            // if customer, only own tickets; if staff/admin, all
            if (requester.getRole() == Role.CUSTOMER) {
                tickets = ticketRepository.findByCreatorId(requester.getId());
            } else {
                tickets = ticketRepository.findAll();
            }
        }
        // enforce authorization on returned set for customer if filtered differently
        if (requester.getRole() == Role.CUSTOMER) {
            tickets = tickets.stream().filter(t -> t.getCreator().getId().equals(requester.getId())).collect(Collectors.toList());
        }
        return tickets.stream().map(this::toDto).collect(Collectors.toList());
    }

    public TicketDto get(Long id, String requesterEmail) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (requester.getRole() == Role.CUSTOMER && !ticket.getCreator().getId().equals(requester.getId())) {
            throw new ForbiddenException("Not authorized to view this ticket");
        }
        return toDto(ticket);
    }

    @Transactional
    public TicketDto update(Long id, TicketUpdateRequest req, String requesterEmail) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (requester.getRole() == Role.CUSTOMER && !ticket.getCreator().getId().equals(requester.getId())) {
            throw new ForbiddenException("Not authorized");
        }
        if (requester.getRole() == Role.CUSTOMER) {
            // customers can update limited fields? allow description/title maybe not status/assignee
            if (req.getStatus() != null || req.getAssigneeId() != null || req.getPriority() != null || req.getCategory() != null) {
                // but easier to just prevent changing status/assignee as staff action; allow title/desc if desired - but spec says update; keep simple
                // prevent status changes by customer
                if (req.getStatus() != null && req.getStatus() != ticket.getStatus()) {
                    throw new ForbiddenException("Customers cannot change status");
                }
                if (req.getAssigneeId() != null) {
                    throw new ForbiddenException("Customers cannot assign tickets");
                }
            }
        }
        boolean statusChanged = false;
        TicketStatus oldStatus = ticket.getStatus();
        if (req.getTitle() != null) ticket.setTitle(req.getTitle());
        if (req.getDescription() != null) ticket.setDescription(req.getDescription());
        if (req.getCategory() != null) ticket.setCategory(req.getCategory());
        if (req.getPriority() != null) ticket.setPriority(req.getPriority());
        if (req.getStatus() != null && req.getStatus() != oldStatus) {
            if (requester.getRole() == Role.CUSTOMER) {
                throw new ForbiddenException("Customers cannot change status");
            }
            ticket.setStatus(req.getStatus());
            statusChanged = true;
        }
        if (req.getAssigneeId() != null && requester.getRole() != Role.CUSTOMER) {
            User assignee = userRepository.findById(req.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found"));
            ticket.setAssignee(assignee);
        }
        ticket = ticketRepository.save(ticket);
        if (statusChanged) {
            TicketStatusHistory hist = TicketStatusHistory.builder()
                    .ticket(ticket)
                    .fromStatus(oldStatus)
                    .toStatus(ticket.getStatus())
                    .changedBy(requester)
                    .changedAt(Instant.now())
                    .build();
            historyRepository.save(hist);
        }
        return toDto(ticket);
    }

    public List<TicketStatusHistoryDto> getHistory(Long id, String requesterEmail) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
        User requester = userRepository.findByEmail(requesterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (requester.getRole() == Role.CUSTOMER && !ticket.getCreator().getId().equals(requester.getId())) {
            throw new ForbiddenException("Not authorized to view history");
        }
        return historyRepository.findByTicketIdOrderByChangedAtAsc(id).stream()
                .map(h -> TicketStatusHistoryDto.builder()
                        .id(h.getId())
                        .fromStatus(h.getFromStatus())
                        .toStatus(h.getToStatus())
                        .changedById(h.getChangedBy().getId())
                        .changedByName(h.getChangedBy().getFullName())
                        .changedAt(h.getChangedAt())
                        .build())
                .collect(Collectors.toList());
    }

    private TicketDto toDto(Ticket t) {
        return TicketDto.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .category(t.getCategory())
                .priority(t.getPriority())
                .status(t.getStatus())
                .creatorId(t.getCreator() != null ? t.getCreator().getId() : null)
                .creatorName(t.getCreator() != null ? t.getCreator().getFullName() : null)
                .assigneeId(t.getAssignee() != null ? t.getAssignee().getId() : null)
                .assigneeName(t.getAssignee() != null ? t.getAssignee().getFullName() : null)
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}
