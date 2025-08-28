package com.fleetstudio.Employee.Suggestion.controller;


import com.fleetstudio.Employee.Suggestion.model.Suggestion;
import com.fleetstudio.Employee.Suggestion.model.SuggestionStatus;
import com.fleetstudio.Employee.Suggestion.model.SuggestionStatusHistory;
import com.fleetstudio.Employee.Suggestion.service.SuggestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
     * Get all suggestions with optional pagination
     */
    @GetMapping
    public ResponseEntity<?> getAllSuggestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean paginated) {
        
        if (paginated) {
            Page<Suggestion> suggestions = suggestionService.getAllSuggestions(page, size);
            return ResponseEntity.ok(suggestions);
        } else {
            List<Suggestion> suggestions = suggestionService.getAllSuggestions();
            return ResponseEntity.ok(suggestions);
        }
    }

    /**
     * Get suggestion by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getSuggestionById(@PathVariable Long id) {
        Optional<Suggestion> suggestion = suggestionService.getSuggestionById(id);
        
        if (suggestion.isPresent()) {
            return ResponseEntity.ok(suggestion.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create new suggestion
     */
    @PostMapping
    public ResponseEntity<?> createSuggestion(@Valid @RequestBody CreateSuggestionRequest request) {
        try {
            Suggestion suggestion = suggestionService.createSuggestion(
                request.getTitle(),
                request.getDescription(),
                request.getEmployeeId(),
                request.isAnonymous()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(suggestion);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Failed to create suggestion"));
        }
    }

    /**
     * Update suggestion (only title and description)
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
            return ResponseEntity.internalServerError().body(new ErrorResponse("Failed to update suggestion"));
        }
    }

    /**
     * Get suggestions by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getSuggestionsByStatus(
            @PathVariable SuggestionStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "false") boolean paginated) {
        
        if (paginated) {
            Page<Suggestion> suggestions = suggestionService.getSuggestionsByStatus(status, page, size);
            return ResponseEntity.ok(suggestions);
        } else {
            List<Suggestion> suggestions = suggestionService.getSuggestionsByStatus(status);
            return ResponseEntity.ok(suggestions);
        }
    }

    /**
     * Get suggestions by employee
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Suggestion>> getSuggestionsByEmployee(@PathVariable Long employeeId) {
        try {
            List<Suggestion> suggestions = suggestionService.getSuggestionsByEmployee(employeeId);
            return ResponseEntity.ok(suggestions);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get anonymous suggestions
     */
    @GetMapping("/anonymous")
    public ResponseEntity<List<Suggestion>> getAnonymousSuggestions() {
        List<Suggestion> suggestions = suggestionService.getAnonymousSuggestions();
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Search suggestions
     */
    @GetMapping("/search")
    public ResponseEntity<List<Suggestion>> searchSuggestions(@RequestParam String q) {
        List<Suggestion> suggestions = suggestionService.searchSuggestions(q);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Get top suggestions by vote count
     */
    @GetMapping("/top")
    public ResponseEntity<Page<Suggestion>> getTopSuggestions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Suggestion> suggestions = suggestionService.getTopSuggestions(page, size);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Get recent suggestions
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Suggestion>> getRecentSuggestions(
            @RequestParam(defaultValue = "7") int days) {
        List<Suggestion> suggestions = suggestionService.getRecentSuggestions(days);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Get popular suggestions (with minimum vote threshold)
     */
    @GetMapping("/popular")
    public ResponseEntity<List<Suggestion>> getPopularSuggestions(
            @RequestParam(defaultValue = "5") int minVotes) {
        List<Suggestion> suggestions = suggestionService.getPopularSuggestions(minVotes);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Get suggestion statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getSuggestionStatistics() {
        Map<String, Long> statistics = suggestionService.getSuggestionStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get suggestion status history
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<List<SuggestionStatusHistory>> getSuggestionHistory(@PathVariable Long id) {
        try {
            List<SuggestionStatusHistory> history = suggestionService.getSuggestionStatusHistory(id);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get suggestion counts by status
     */
    @GetMapping("/counts")
    public ResponseEntity<SuggestionCounts> getSuggestionCounts() {
        SuggestionCounts counts = new SuggestionCounts();
        counts.total = suggestionService.countTotalSuggestions();
        counts.open = suggestionService.countSuggestionsByStatus(SuggestionStatus.OPEN);
        counts.underReview = suggestionService.countSuggestionsByStatus(SuggestionStatus.UNDER_REVIEW);
        counts.implemented = suggestionService.countSuggestionsByStatus(SuggestionStatus.IMPLEMENTED);
        counts.rejected = suggestionService.countSuggestionsByStatus(SuggestionStatus.REJECTED);
        counts.inProgress = suggestionService.countSuggestionsByStatus(SuggestionStatus.IN_PROGRESS);
        counts.onHold = suggestionService.countSuggestionsByStatus(SuggestionStatus.ON_HOLD);
        counts.anonymous = suggestionService.countAnonymousSuggestions();
        
        return ResponseEntity.ok(counts);
    }

    /**
     * Create sample suggestions (for demo purposes)
     */
    @PostMapping("/sample-data")
    public ResponseEntity<?> createSampleSuggestions() {
        try {
            suggestionService.createSampleSuggestions();
            return ResponseEntity.ok(new SuccessResponse("Sample suggestions created successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Failed to create sample suggestions"));
        }
    }

    // Request/Response classes
    public static class CreateSuggestionRequest {
        private String title;
        private String description;
        private Long employeeId;
        private boolean anonymous;

        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Long getEmployeeId() { return employeeId; }
        public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
        
        public boolean isAnonymous() { return anonymous; }
        public void setAnonymous(boolean anonymous) { this.anonymous = anonymous; }
    }

    public static class UpdateSuggestionRequest {
        private String title;
        private String description;

        // Getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class SuggestionCounts {
        public long total;
        public long open;
        public long underReview;
        public long implemented;
        public long rejected;
        public long inProgress;
        public long onHold;
        public long anonymous;
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

    public static class SuccessResponse {
        private String message;
        private long timestamp;

        public SuccessResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessage() { return message; }
        public long getTimestamp() { return timestamp; }
    }
}