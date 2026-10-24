package com.fixflow.ai.service;

import com.fixflow.ai.dto.dashboard.DashboardStatsDto;
import com.fixflow.ai.model.Ticket;
import com.fixflow.ai.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TicketRepository ticketRepository;

    public DashboardStatsDto stats() {
        List<Ticket> all = ticketRepository.findAll();
        long total = all.size();
        Map<String, Long> byStatus = all.stream()
                .collect(Collectors.groupingBy(t -> t.getStatus().name(), Collectors.counting()));
        Map<String, Long> byPriority = all.stream()
                .collect(Collectors.groupingBy(t -> t.getPriority().name(), Collectors.counting()));
        Map<String, Long> byCategory = all.stream()
                .collect(Collectors.groupingBy(t -> t.getCategory().name(), Collectors.counting()));
        Instant cutoff = Instant.now().minus(7, ChronoUnit.DAYS);
        long recent = all.stream().filter(t -> t.getCreatedAt() != null && t.getCreatedAt().isAfter(cutoff)).count();
        return DashboardStatsDto.builder()
                .totalTickets(total)
                .byStatus(byStatus)
                .byPriority(byPriority)
                .byCategory(byCategory)
                .recentTickets(recent)
                .build();
    }
}
