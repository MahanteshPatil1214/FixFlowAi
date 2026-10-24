package com.fixflow.ai.controller;

import com.fixflow.ai.dto.ticket.*;
import com.fixflow.ai.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','STAFF','ADMIN')")
    public ResponseEntity<TicketDto> create(@Valid @RequestBody TicketCreateRequest req, Authentication auth) {
        return ResponseEntity.ok(ticketService.create(req, auth.getName()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','STAFF','ADMIN')")
    public ResponseEntity<List<TicketDto>> list(
            Authentication auth,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String priority
    ) {
        return ResponseEntity.ok(ticketService.list(auth.getName(), q, status, category, priority));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','STAFF','ADMIN')")
    public ResponseEntity<TicketDto> get(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ticketService.get(id, auth.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','STAFF','ADMIN')")
    public ResponseEntity<TicketDto> update(@PathVariable Long id, @Valid @RequestBody TicketUpdateRequest req, Authentication auth) {
        return ResponseEntity.ok(ticketService.update(id, req, auth.getName()));
    }

    @GetMapping("/{id}/history")
    @PreAuthorize("hasAnyRole('CUSTOMER','STAFF','ADMIN')")
    public ResponseEntity<List<TicketStatusHistoryDto>> history(@PathVariable Long id, Authentication auth) {
        return ResponseEntity.ok(ticketService.getHistory(id, auth.getName()));
    }
}
