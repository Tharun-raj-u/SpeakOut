package com.fleetstudio.Employee.Suggestion.controller;

import com.fleetstudio.Employee.Suggestion.dto.SuggestionRequest;
import com.fleetstudio.Employee.Suggestion.dto.SuggestionResponse;
import com.fleetstudio.Employee.Suggestion.model.Suggestion;
import com.fleetstudio.Employee.Suggestion.model.SuggestionStatus;
import com.fleetstudio.Employee.Suggestion.model.SuggestionStatusHistory;
import com.fleetstudio.Employee.Suggestion.security.jwt.UserDetailsImpl;
import com.fleetstudio.Employee.Suggestion.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/suggestions")
@CrossOrigin(origins = "*")
public class SuggestionController {

    private final SuggestionService suggestionService;

    @Autowired
    public SuggestionController(SuggestionService suggestionService) {
        this.suggestionService = suggestionService;
    }

    /**
     * Get all suggestions (with pagination + status filter for Admin)
     */
    @GetMapping
    public ResponseEntity<?> getAllSuggestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean paginated,
            @RequestParam(defaultValue = "ALL") String status,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        boolean isAdmin = userDetails != null && userDetails.getAuthorities()
                .stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (paginated) {
            if (isAdmin && !status.equalsIgnoreCase("ALL")) {
                // Admin + Filter + Paginated
                Page<Suggestion> suggestions = suggestionService.getSuggestionsByStatus(
                        SuggestionStatus.valueOf(status.toUpperCase()), page, size);
                return ResponseEntity.ok(suggestions.map(SuggestionResponse::new));
            } else {
                // Normal Paginated
                Page<Suggestion> suggestions = suggestionService.getAllSuggestions(page, size);
                return ResponseEntity.ok(suggestions.map(SuggestionResponse::new));
            }
        } else {
            if (isAdmin && !status.equalsIgnoreCase("ALL")) {
                // Admin + Filter (non-paginated)
                List<Suggestion> suggestions = suggestionService.getSuggestionsByStatus(
                        SuggestionStatus.valueOf(status.toUpperCase()));
                return ResponseEntity.ok(suggestions.stream().map(SuggestionResponse::new).toList());
            } else {
                // Normal Non-paginated
                List<Suggestion> suggestions = suggestionService.getAllSuggestions();
                return ResponseEntity.ok(suggestions.stream().map(SuggestionResponse::new).toList());
            }
        }
    }

    /**
     * Get suggestion by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getSuggestionById(@PathVariable Long id) {
        Optional<Suggestion> suggestion = suggestionService.getSuggestionById(id);
        return suggestion.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Create new suggestion
     */
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<?> submitSuggestion(@RequestBody SuggestionRequest request,
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long employeeId = userDetails.getId();
        suggestionService.createSuggestion(
                request.getTitle(), request.getDescription(), employeeId, request.isAnonymous());
        return ResponseEntity.ok("Suggestion submitted successfully");
    }

    /**
     * Update suggestion
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSuggestion(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSuggestionRequest request) {
        try {
            Suggestion suggestion = suggestionService.updateSuggestion(
                    id, request.getTitle(), request.getDescription());
            return ResponseEntity.ok(suggestion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(new ErrorResponse("Failed to update suggestion"));
        }
    }

    /**
     * Get suggestions by employee (only his own)
     */
    @GetMapping("/employee")
    public ResponseEntity<List<Suggestion>> getSuggestionsByEmployee(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        Long employeeId = userDetails.getId();
        List<Suggestion> suggestions = suggestionService.getSuggestionsByEmployee(employeeId);
        return ResponseEntity.ok(suggestions);
    }

    // ... keep the rest of endpoints same (anonymous, search, top, recent, etc.)

    // --- Request/Response helper classes ---
    public static class UpdateSuggestionRequest {
        private String title;
        private String description;
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class ErrorResponse {
        private String message;
        private long timestamp;
        public ErrorResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }
        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }
}
