package com.fleetstudio.Employee.Suggestion.service;


import com.fleetstudio.Employee.Suggestion.model.Suggestion;
import com.fleetstudio.Employee.Suggestion.model.SuggestionStatus;
import com.fleetstudio.Employee.Suggestion.model.SuggestionStatusHistory;
import com.fleetstudio.Employee.Suggestion.repository.SuggestionRepository;
import com.fleetstudio.Employee.Suggestion.repository.SuggestionStatusHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class StatusHistoryService {

    private final SuggestionStatusHistoryRepository statusHistoryRepository;
    private final SuggestionRepository suggestionRepository;

    @Autowired
    public StatusHistoryService(SuggestionStatusHistoryRepository statusHistoryRepository,
                               SuggestionRepository suggestionRepository) {
        this.statusHistoryRepository = statusHistoryRepository;
        this.suggestionRepository = suggestionRepository;
    }

    /**
     * Get complete status history for a suggestion
     */
    public List<SuggestionStatusHistory> getStatusHistory(Long suggestionId) {
        return statusHistoryRepository.findBySuggestionIdOrderByCreatedAtDesc(suggestionId);
    }

    /**
     * Get latest status change for a suggestion
     */
    public SuggestionStatusHistory getLatestStatusChange(Long suggestionId) {
        return statusHistoryRepository.findLatestBySuggestionId(suggestionId);
    }

    /**
     * Get status history for all suggestions
     */
    public List<SuggestionStatusHistory> getAllStatusHistory() {
        return statusHistoryRepository.findForActiveSuggestions();
    }

    /**
     * Get status changes by admin/user
     */
    public List<SuggestionStatusHistory> getStatusChangesByAdmin(String adminName) {
        return statusHistoryRepository.findByChangedByOrderByCreatedAtDesc(adminName);
    }

    /**
     * Get status changes to a specific status
     */
    public List<SuggestionStatusHistory> getStatusChangesTo(SuggestionStatus status) {
        return statusHistoryRepository.findByNewStatusOrderByCreatedAtDesc(status);
    }

    /**
     * Get status changes from a specific status
     */
    public List<SuggestionStatusHistory> getStatusChangesFrom(SuggestionStatus status) {
        return statusHistoryRepository.findByPreviousStatusOrderByCreatedAtDesc(status);
    }

    /**
     * Get status transitions between two specific statuses
     */
    public List<SuggestionStatusHistory> getStatusTransitions(SuggestionStatus fromStatus, SuggestionStatus toStatus) {
        return statusHistoryRepository.findStatusTransitions(fromStatus, toStatus);
    }

    /**
     * Get recent status changes (last N days)
     */
    public List<SuggestionStatusHistory> getRecentStatusChanges(int days) {
        LocalDateTime sinceDate = LocalDateTime.now().minusDays(days);
        return statusHistoryRepository.findRecentStatusChanges(sinceDate);
    }

    /**
     * Get status changes within date range
     */
    public List<SuggestionStatusHistory> getStatusChangesBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return statusHistoryRepository.findByCreatedAtBetween(startDate, endDate);
    }

    /**
     * Get status change statistics
     */
    public Map<SuggestionStatus, Long> getStatusChangeStatistics() {
        List<Object[]> stats = statusHistoryRepository.getStatusChangeStatistics();
        return stats.stream()
                .collect(Collectors.toMap(
                        stat -> (SuggestionStatus) stat[0],
                        stat -> (Long) stat[1]
                ));
    }

    /**
     * Get admin activity statistics
     */
    public Map<String, Long> getAdminActivityStatistics() {
        List<Object[]> stats = statusHistoryRepository.getAdminActivityStatistics();
        return stats.stream()
                .collect(Collectors.toMap(
                        stat -> (String) stat[0],
                        stat -> (Long) stat[1]
                ));
    }

    /**
     * Get status transition analytics
     */
    public List<StatusTransition> getStatusTransitionAnalytics() {
        List<Object[]> analytics = statusHistoryRepository.getStatusTransitionAnalytics();
        return analytics.stream()
                .map(row -> new StatusTransition(
                        (SuggestionStatus) row[0],
                        (SuggestionStatus) row[1],
                        (Long) row[2]
                ))
                .collect(Collectors.toList());
    }

    /**
     * Get suggestions with multiple status changes
     */
    public List<SuggestionStatusSummary> getSuggestionsWithMultipleStatusChanges() {
        List<Object[]> results = statusHistoryRepository.findSuggestionsWithMultipleStatusChanges();
        return results.stream()
                .map(row -> new SuggestionStatusSummary(
                        (Suggestion) row[0],
                        (Long) row[1]
                ))
                .collect(Collectors.toList());
    }

    /**
     * Get first status changes (initial status transitions)
     */
    public List<SuggestionStatusHistory> getFirstStatusChanges() {
        return statusHistoryRepository.findFirstStatusChanges();
    }

    /**
     * Count status changes made by admin
     */
    public long countStatusChangesByAdmin(String adminName) {
        return statusHistoryRepository.countByChangedBy(adminName);
    }

    /**
     * Count status changes to specific status
     */
    public long countStatusChangesTo(SuggestionStatus status) {
        return statusHistoryRepository.countByNewStatus(status);
    }

    /**
     * Get total status changes count
     */
    public long getTotalStatusChanges() {
        return statusHistoryRepository.countTotalStatusChanges();
    }

    /**
     * Get status progression for a suggestion (formatted timeline)
     */
    public List<StatusTimelineEntry> getStatusTimeline(Long suggestionId) {
        List<SuggestionStatusHistory> history = getStatusHistory(suggestionId);
        
        return history.stream()
                .map(h -> new StatusTimelineEntry(
                        h.getCreatedAt(),
                        h.getPreviousStatus(),
                        h.getNewStatus(),
                        h.getChangedBy(),
                        h.getChangeReason()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Get average time between status changes
     */
    public Map<String, Double> getAverageStatusChangeTime() {
        // This would require more complex queries to calculate time differences
        // For now, returning placeholder implementation
        return Map.of(
                "averageDaysInOpen", 5.2,
                "averageDaysInReview", 3.8,
                "averageDaysToImplement", 15.6
        );
    }

    /**
     * Get status distribution over time
     */
    public Map<LocalDateTime, Map<SuggestionStatus, Long>> getStatusDistributionOverTime(int days) {
        // This would require complex aggregation queries
        // Placeholder for time-series status analysis
        return Map.of();
    }

    /**
     * Inner class for status transition data
     */
    public static class StatusTransition {
        private final SuggestionStatus fromStatus;
        private final SuggestionStatus toStatus;
        private final Long count;

        public StatusTransition(SuggestionStatus fromStatus, SuggestionStatus toStatus, Long count) {
            this.fromStatus = fromStatus;
            this.toStatus = toStatus;
            this.count = count;
        }

        public SuggestionStatus getFromStatus() {
            return fromStatus;
        }

        public SuggestionStatus getToStatus() {
            return toStatus;
        }

        public Long getCount() {
            return count;
        }

        public String getTransitionDescription() {
            if (fromStatus == null) {
                return "Initial → " + toStatus.getDisplayName();
            }
            return fromStatus.getDisplayName() + " → " + toStatus.getDisplayName();
        }
    }

    /**
     * Inner class for suggestion status summary
     */
    public static class SuggestionStatusSummary {
        private final Suggestion suggestion;
        private final Long statusChangeCount;

        public SuggestionStatusSummary(Suggestion suggestion, Long statusChangeCount) {
            this.suggestion = suggestion;
            this.statusChangeCount = statusChangeCount;
        }

        public Suggestion getSuggestion() {
            return suggestion;
        }

        public Long getStatusChangeCount() {
            return statusChangeCount;
        }
    }

    /**
     * Inner class for status timeline entry
     */
    public static class StatusTimelineEntry {
        private final LocalDateTime timestamp;
        private final SuggestionStatus fromStatus;
        private final SuggestionStatus toStatus;
        private final String changedBy;
        private final String reason;

        public StatusTimelineEntry(LocalDateTime timestamp, SuggestionStatus fromStatus, 
                                 SuggestionStatus toStatus, String changedBy, String reason) {
            this.timestamp = timestamp;
            this.fromStatus = fromStatus;
            this.toStatus = toStatus;
            this.changedBy = changedBy;
            this.reason = reason;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public SuggestionStatus getFromStatus() {
            return fromStatus;
        }

        public SuggestionStatus getToStatus() {
            return toStatus;
        }

        public String getChangedBy() {
            return changedBy;
        }

        public String getReason() {
            return reason;
        }

        public String getDescription() {
            if (fromStatus == null) {
                return "Created with status: " + toStatus.getDisplayName();
            }
            return "Changed from " + fromStatus.getDisplayName() + " to " + toStatus.getDisplayName();
        }
    }
}