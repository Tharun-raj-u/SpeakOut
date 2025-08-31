package com.fleetstudio.Employee.Suggestion.controller;

import com.fleetstudio.Employee.Suggestion.model.SuggestionStatus;
import com.fleetstudio.Employee.Suggestion.model.SuggestionStatusHistory;
import com.fleetstudio.Employee.Suggestion.service.StatusHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/status-history")
@CrossOrigin(origins = "*")
public class StatusHistoryController {

    private final StatusHistoryService statusHistoryService;

    @Autowired
    public StatusHistoryController(StatusHistoryService statusHistoryService) {
        this.statusHistoryService = statusHistoryService;
    }

    /**
     * Get status history for a specific suggestion
     */
    @GetMapping("/suggestion/{suggestionId}")
    public ResponseEntity<List<SuggestionStatusHistory>> getStatusHistory(@PathVariable Long suggestionId) {
        try {
            List<SuggestionStatusHistory> history = statusHistoryService.getStatusHistory(suggestionId);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get status timeline for a specific suggestion (formatted)
     */
    @GetMapping("/suggestion/{suggestionId}/timeline")
    public ResponseEntity<List<StatusHistoryService.StatusTimelineEntry>> getStatusTimeline(@PathVariable Long suggestionId) {
        try {
            List<StatusHistoryService.StatusTimelineEntry> timeline = statusHistoryService.getStatusTimeline(suggestionId);
            return ResponseEntity.ok(timeline);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get latest status change for a suggestion
     */
    @GetMapping("/suggestion/{suggestionId}/latest")
    public ResponseEntity<SuggestionStatusHistory> getLatestStatusChange(@PathVariable Long suggestionId) {
        try {
            SuggestionStatusHistory latest = statusHistoryService.getLatestStatusChange(suggestionId);
            if (latest != null) {
                return ResponseEntity.ok(latest);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all status history for active suggestions
     */
    @GetMapping
    public ResponseEntity<List<SuggestionStatusHistory>> getAllStatusHistory() {
        List<SuggestionStatusHistory> history = statusHistoryService.getAllStatusHistory();
        return ResponseEntity.ok(history);
    }

    /**
     * Get status changes by admin
     */
    @GetMapping("/admin/{adminName}")
    public ResponseEntity<List<SuggestionStatusHistory>> getStatusChangesByAdmin(@PathVariable String adminName) {
        List<SuggestionStatusHistory> changes = statusHistoryService.getStatusChangesByAdmin(adminName);
        return ResponseEntity.ok(changes);
    }

    /**
     * Get status changes to a specific status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<SuggestionStatusHistory>> getStatusChangesTo(@PathVariable SuggestionStatus status) {
        List<SuggestionStatusHistory> changes = statusHistoryService.getStatusChangesTo(status);
        return ResponseEntity.ok(changes);
    }

    /**
     * Get status transitions between two statuses
     */
    @GetMapping("/transitions")
    public ResponseEntity<List<SuggestionStatusHistory>> getStatusTransitions(
            @RequestParam SuggestionStatus from,
            @RequestParam SuggestionStatus to) {
        List<SuggestionStatusHistory> transitions = statusHistoryService.getStatusTransitions(from, to);
        return ResponseEntity.ok(transitions);
    }

    /**
     * Get recent status changes
     */
    @GetMapping("/recent")
    public ResponseEntity<List<SuggestionStatusHistory>> getRecentStatusChanges(
            @RequestParam(defaultValue = "7") int days) {
        List<SuggestionStatusHistory> changes = statusHistoryService.getRecentStatusChanges(days);
        return ResponseEntity.ok(changes);
    }

    /**
     * Get status changes within date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<SuggestionStatusHistory>> getStatusChangesBetween(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            LocalDateTime start = LocalDateTime.parse(startDate, formatter);
            LocalDateTime end = LocalDateTime.parse(endDate, formatter);
            
            List<SuggestionStatusHistory> changes = statusHistoryService.getStatusChangesBetween(start, end);
            return ResponseEntity.ok(changes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get status change statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<SuggestionStatus, Long>> getStatusChangeStatistics() {
        Map<SuggestionStatus, Long> statistics = statusHistoryService.getStatusChangeStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get admin activity statistics
     */
    @GetMapping("/statistics/admin-activity")
    public ResponseEntity<Map<String, Long>> getAdminActivityStatistics() {
        Map<String, Long> statistics = statusHistoryService.getAdminActivityStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get status transition analytics
     */
    @GetMapping("/analytics/transitions")
    public ResponseEntity<List<StatusHistoryService.StatusTransition>> getStatusTransitionAnalytics() {
        List<StatusHistoryService.StatusTransition> analytics = statusHistoryService.getStatusTransitionAnalytics();
        return ResponseEntity.ok(analytics);
    }

    /**
     * Get suggestions with multiple status changes
     */
    @GetMapping("/analytics/frequent-changes")
    public ResponseEntity<List<StatusHistoryService.SuggestionStatusSummary>> getSuggestionsWithMultipleStatusChanges() {
        List<StatusHistoryService.SuggestionStatusSummary> suggestions = 
            statusHistoryService.getSuggestionsWithMultipleStatusChanges();
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Get first status changes (initial transitions from default status)
     */
    @GetMapping("/first-changes")
    public ResponseEntity<List<SuggestionStatusHistory>> getFirstStatusChanges() {
        List<SuggestionStatusHistory> changes = statusHistoryService.getFirstStatusChanges();
        return ResponseEntity.ok(changes);
    }

    /**
     * Count status changes by admin
     */
    @GetMapping("/count/admin/{adminName}")
    public ResponseEntity<StatusChangeCount> countStatusChangesByAdmin(@PathVariable String adminName) {
        long count = statusHistoryService.countStatusChangesByAdmin(adminName);
        return ResponseEntity.ok(new StatusChangeCount(count));
    }

    /**
     * Count status changes to specific status
     */
    @GetMapping("/count/status/{status}")
    public ResponseEntity<StatusChangeCount> countStatusChangesTo(@PathVariable SuggestionStatus status) {
        long count = statusHistoryService.countStatusChangesTo(status);
        return ResponseEntity.ok(new StatusChangeCount(count));
    }

    /**
     * Get total status changes count
     */
    @GetMapping("/count/total")
    public ResponseEntity<StatusChangeCount> getTotalStatusChanges() {
        long count = statusHistoryService.getTotalStatusChanges();
        return ResponseEntity.ok(new StatusChangeCount(count));
    }

    /**
     * Get average status change times (placeholder implementation)
     */
    @GetMapping("/analytics/timing")
    public ResponseEntity<Map<String, Double>> getAverageStatusChangeTime() {
        Map<String, Double> timing = statusHistoryService.getAverageStatusChangeTime();
        return ResponseEntity.ok(timing);
    }

    /**
     * Get activity summary for a specific period
     */
    @GetMapping("/summary")
    public ResponseEntity<ActivitySummary> getActivitySummary(
            @RequestParam(defaultValue = "30") int days) {
        
        ActivitySummary summary = new ActivitySummary();
        summary.totalChanges = statusHistoryService.getTotalStatusChanges();
        summary.recentChanges = statusHistoryService.getRecentStatusChanges(days).size();
        summary.statusStatistics = statusHistoryService.getStatusChangeStatistics();
        summary.adminActivity = statusHistoryService.getAdminActivityStatistics();
        summary.periodDays = days;
        
        return ResponseEntity.ok(summary);
    }

    // Response classes
    public static class StatusChangeCount {
        private long count;
        private long timestamp;

        public StatusChangeCount(long count) {
            this.count = count;
            this.timestamp = System.currentTimeMillis();
        }

        public long getCount() { return count; }
        public long getTimestamp() { return timestamp; }
    }

    public static class ActivitySummary {
        public long totalChanges;
        public int recentChanges;
        public int periodDays;
        public Map<SuggestionStatus, Long> statusStatistics;
        public Map<String, Long> adminActivity;
    }
}