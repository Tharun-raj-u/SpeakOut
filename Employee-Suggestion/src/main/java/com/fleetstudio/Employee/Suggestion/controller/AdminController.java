package com.fleetstudio.Employee.Suggestion.controller;

import com.fleetstudio.Employee.Suggestion.model.SuggestionStatus;
import com.fleetstudio.Employee.Suggestion.security.jwt.UserDetailsImpl;
import com.fleetstudio.Employee.Suggestion.service.AdminService;
import com.fleetstudio.Employee.Suggestion.service.StatusHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.fleetstudio.Employee.Suggestion.model.Suggestion;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final StatusHistoryService statusHistoryService;

    @Autowired
    public AdminController(AdminService adminService, StatusHistoryService statusHistoryService) {
        this.adminService = adminService;
        this.statusHistoryService = statusHistoryService;
    }


    /**
     * Get admin dashboard statistics
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDashboard() {
        AdminService.DashboardStatistics stats = adminService.getDashboardStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Change suggestion status
     */
    @PutMapping("/suggestions/{suggestionId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> changeStatus(
            @PathVariable Long suggestionId,
            @RequestBody ChangeStatusRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        try {
            boolean success = adminService.changeStatus(
                    suggestionId,
                    request.getStatus(),
                    userDetails.getUsername(),
                    request.getReason()
            );

            if (success) {
                return ResponseEntity.ok(new SuccessResponse("Status updated successfully"));
            } else {
                return ResponseEntity.badRequest().body(new ErrorResponse("Failed to update status"));
            }

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
              e.printStackTrace();
            return ResponseEntity.internalServerError().body(new ErrorResponse("Internal server error"));
        }
    }

    /**
     * Delete suggestion (soft delete)
     */
    @DeleteMapping("/suggestions/{suggestionId}")

    public ResponseEntity<?> deleteSuggestion(
            @PathVariable Long suggestionId,@AuthenticationPrincipal  UserDetailsImpl userDetails) {


        try {
            boolean success = adminService.deleteSuggestion(suggestionId, userDetails.getUsername());
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

    @GetMapping("/suggestions/deleted")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Suggestion>> getDeletedSuggestions() {
        try {
            List<Suggestion> deleted = adminService.getDeletedSuggestions();
            return ResponseEntity.ok(deleted);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/suggestions/hardDelete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> hardDeletedSuggestions() {
        try {
            adminService.hardDeleteSuggestions(); // Call the service method
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error while deleting suggestions: " + e.getMessage());
        }
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