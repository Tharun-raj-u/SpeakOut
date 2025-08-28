package com.fleetstudio.Employee.Suggestion.controller;

import com.fleetstudio.Employee.Suggestion.model.SuggestionStatus;
import com.fleetstudio.Employee.Suggestion.service.AdminService;
import com.fleetstudio.Employee.Suggestion.service.StatusHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;
    private final StatusHistoryService statusHistoryService;

    @Autowired
    public AdminController(AdminService adminService, StatusHistoryService statusHistoryService) {
        this.adminService = adminService;
        this.statusHistoryService = statusHistoryService;
    }

    /**
     * Validate admin access
     */
    @GetMapping("/validate")
    public ResponseEntity<AdminValidationResponse> validateAdmin(@RequestParam String token) {
        boolean isAdmin = adminService.isAdmin(token);
        return ResponseEntity.ok(new AdminValidationResponse(isAdmin));
    }

    /**
     * Get admin dashboard statistics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@RequestParam String token) {
        if (!adminService.isAdmin(token)) {
            return ResponseEntity.status(403).body(new ErrorResponse("Admin access required"));
        }

        AdminService.DashboardStatistics stats = adminService.getDashboardStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Change suggestion status
     */
    @PutMapping("/suggestions/{suggestionId}/status")
    public ResponseEntity<?> changeStatus(
            @PathVariable Long suggestionId,
            @RequestBody ChangeStatusRequest request,
            @RequestParam String token) {
        
        if (!adminService.isAdmin(token)) {
            return ResponseEntity.status(403).body(new ErrorResponse("Admin access required"));
        }

        try {
            boolean success = adminService.changeStatus(suggestionId, request.getStatus(), token, request.getReason());
            if (success) {
                return ResponseEntity.ok(new SuccessResponse("Status updated successfully"));
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponse("Failed to update status"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Internal server error"));
        }
    }

    /**
     * Delete suggestion (soft delete)
     */
    @DeleteMapping("/suggestions/{suggestionId}")
    public ResponseEntity<?> deleteSuggestion(
            @PathVariable Long suggestionId,
            @RequestParam String token,
            @RequestParam(required = false) String reason) {
        
        if (!adminService.isAdmin(token)) {
            return ResponseEntity.status(403).body(new ErrorResponse("Admin access required"));
        }

        try {
            boolean success = adminService.deleteSuggestion(suggestionId, token, reason);
            if (success) {
                return ResponseEntity.ok(new SuccessResponse("Suggestion deleted successfully"));
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponse("Failed to delete suggestion"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Internal server error"));
        }
    }

    /**
     * Bulk update suggestion statuses
     */
    @PutMapping("/suggestions/bulk-status")
    public ResponseEntity<?> bulkUpdateStatus(
            @RequestBody BulkStatusUpdateRequest request,
            @RequestParam String token) {
        
        if (!adminService.isAdmin(token)) {
            return ResponseEntity.status(403).body(new ErrorResponse("Admin access required"));
        }

        try {
            AdminService.BulkUpdateResult result = adminService.bulkUpdateStatus(
                request.getSuggestionIds(), 
                request.getStatus(), 
                token, 
                request.getReason()
            );
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Bulk update failed"));
        }
    }

    /**
     * Initialize sample data
     */
    @PostMapping("/init-sample-data")
    public ResponseEntity<?> initializeSampleData(@RequestParam String token) {
        if (!adminService.isAdmin(token)) {
            return ResponseEntity.status(403).body(new ErrorResponse("Admin access required"));
        }

        try {
            adminService.initializeSampleData();
            return ResponseEntity.ok(new SuccessResponse("Sample data initialized successfully"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse("Failed to initialize sample data"));
        }
    }

    /**
     * Perform system maintenance
     */
    @PostMapping("/maintenance")
    public ResponseEntity<?> performMaintenance(@RequestParam String token) {
        if (!adminService.isAdmin(token)) {
            return ResponseEntity.status(403).body(new ErrorResponse("Admin access required"));
        }

        AdminService.MaintenanceResult result = adminService.performMaintenance(token);
        return ResponseEntity.ok(result);
    }

    /**
     * Export suggestions data
     */
    @PostMapping("/export")
    public ResponseEntity<?> exportData(
            @RequestParam String token,
            @RequestParam(defaultValue = "json") String format) {
        
        if (!adminService.isAdmin(token)) {
            return ResponseEntity.status(403).body(new ErrorResponse("Admin access required"));
        }

        AdminService.ExportResult result = adminService.exportSuggestions(format, token);
        return ResponseEntity.ok(result);
    }

    /**
     * Get system health check
     */
    @GetMapping("/health")
    public ResponseEntity<AdminService.SystemHealthCheck> getSystemHealth(@RequestParam String token) {
        if (!adminService.isAdmin(token)) {
            return ResponseEntity.status(403).build();
        }

        AdminService.SystemHealthCheck health = adminService.getSystemHealth();
        return ResponseEntity.ok(health);
    }

    /**
     * Get status change history analytics
     */
    @GetMapping("/analytics/status-transitions")
    public ResponseEntity<?> getStatusTransitionAnalytics(@RequestParam String token) {
        if (!adminService.isAdmin(token)) {
            return ResponseEntity.status(403).body(new ErrorResponse("Admin access required"));
        }

        List<StatusHistoryService.StatusTransition> analytics = statusHistoryService.getStatusTransitionAnalytics();
        return ResponseEntity.ok(analytics);
    }

    /**
     * Get admin activity statistics
     */
    @GetMapping("/analytics/admin-activity")
    public ResponseEntity<?> getAdminActivityAnalytics(@RequestParam String token) {
        if (!adminService.isAdmin(token)) {
            return ResponseEntity.status(403).body(new ErrorResponse("Admin access required"));
        }

        return ResponseEntity.ok(statusHistoryService.getAdminActivityStatistics());
    }

    /**
     * Get suggestions with multiple status changes
     */
    @GetMapping("/analytics/frequent-changes")
    public ResponseEntity<?> getFrequentlyChangedSuggestions(@RequestParam String token) {
        if (!adminService.isAdmin(token)) {
            return ResponseEntity.status(403).body(new ErrorResponse("Admin access required"));
        }

        List<StatusHistoryService.SuggestionStatusSummary> suggestions = 
            statusHistoryService.getSuggestionsWithMultipleStatusChanges();
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Get recent status changes for monitoring
     */
    @GetMapping("/recent-changes")
    public ResponseEntity<?> getRecentStatusChanges(
            @RequestParam String token,
            @RequestParam(defaultValue = "7") int days) {
        
        if (!adminService.isAdmin(token)) {
            return ResponseEntity.status(403).body(new ErrorResponse("Admin access required"));
        }

        return ResponseEntity.ok(statusHistoryService.getRecentStatusChanges(days));
    }

    /**
     * Get available suggestion statuses
     */
    @GetMapping("/statuses")
    public ResponseEntity<SuggestionStatus[]> getAvailableStatuses() {
        return ResponseEntity.ok(SuggestionStatus.values());
    }

    // Request/Response classes
    public static class ChangeStatusRequest {
        private SuggestionStatus status;
        private String reason;

        public SuggestionStatus getStatus() { return status; }
        public void setStatus(SuggestionStatus status) { this.status = status; }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class BulkStatusUpdateRequest {
        private Long[] suggestionIds;
        private SuggestionStatus status;
        private String reason;

        public Long[] getSuggestionIds() { return suggestionIds; }
        public void setSuggestionIds(Long[] suggestionIds) { this.suggestionIds = suggestionIds; }
        
        public SuggestionStatus getStatus() { return status; }
        public void setStatus(SuggestionStatus status) { this.status = status; }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public static class AdminValidationResponse {
        private boolean isAdmin;
        private long timestamp;

        public AdminValidationResponse(boolean isAdmin) {
            this.isAdmin = isAdmin;
            this.timestamp = System.currentTimeMillis();
        }

        public boolean isAdmin() { return isAdmin; }
        public long getTimestamp() { return timestamp; }
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