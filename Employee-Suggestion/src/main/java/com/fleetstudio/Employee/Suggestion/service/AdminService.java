package com.fleetstudio.Employee.Suggestion.service;


import com.fleetstudio.Employee.Suggestion.model.Suggestion;
import com.fleetstudio.Employee.Suggestion.model.SuggestionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AdminService {

    private final SuggestionService suggestionService;
    private final VoteService voteService;
    private final StatusHistoryService statusHistoryService;
    private final EmployeeService employeeService;

    @Autowired
    public AdminService(SuggestionService suggestionService,
                       VoteService voteService,
                       StatusHistoryService statusHistoryService,
                       EmployeeService employeeService) {
        this.suggestionService = suggestionService;
        this.voteService = voteService;
        this.statusHistoryService = statusHistoryService;
        this.employeeService = employeeService;
    }

    /**
     * Check if user has admin privileges (simple implementation for demo)
     */
    public boolean isAdmin(String adminToken) {
        // Simple demo implementation - in production, this would check actual authentication
        return "admin123".equals(adminToken) || "admin".equals(adminToken);
    }

    /**
     * Get comprehensive dashboard statistics
     */
    @Transactional(readOnly = true)
    public DashboardStatistics getDashboardStatistics() {
        DashboardStatistics stats = new DashboardStatistics();
        
        // Suggestion statistics
        stats.totalSuggestions = suggestionService.countTotalSuggestions();
        stats.anonymousSuggestions = suggestionService.countAnonymousSuggestions();
        stats.openSuggestions = suggestionService.countSuggestionsByStatus(SuggestionStatus.OPEN);
        stats.underReviewSuggestions = suggestionService.countSuggestionsByStatus(SuggestionStatus.UNDER_REVIEW);
        stats.implementedSuggestions = suggestionService.countSuggestionsByStatus(SuggestionStatus.IMPLEMENTED);
        stats.rejectedSuggestions = suggestionService.countSuggestionsByStatus(SuggestionStatus.REJECTED);
        
        // Voting statistics
        Map<String, Object> votingStats = voteService.getVotingEngagementStatistics();
        stats.totalVotes = (Long) votingStats.get("totalVotes");
        stats.uniqueVoters = (Long) votingStats.get("uniqueVoters");
        stats.averageVotesPerSuggestion = (Double) votingStats.get("averageVotesPerSuggestion");
        
        // Recent activity
        stats.recentSuggestions7Days = suggestionService.getRecentSuggestions(7).size();
        stats.recentVotes7Days = voteService.getRecentVotes(7).size();
        stats.recentStatusChanges7Days = statusHistoryService.getRecentStatusChanges(7).size();
        
        // Employee statistics
        stats.totalEmployees = employeeService.getTotalEmployeeCount();
        
        // Status change statistics
        stats.statusChangeStatistics = statusHistoryService.getStatusChangeStatistics();
        stats.adminActivityStatistics = statusHistoryService.getAdminActivityStatistics();
        
        return stats;
    }

    /**
     * Change suggestion status with admin verification
     */
    public boolean changeStatus(Long suggestionId, SuggestionStatus newStatus, String adminToken, String reason) {

        suggestionService.changeStatus(suggestionId, newStatus, adminToken, reason);
        return true;
    }

    /**
     * Delete suggestion with admin verification
     */
    public boolean deleteSuggestion(Long suggestionId, String adminName) {

        suggestionService.deleteSuggestion(suggestionId, adminName);
        return true;
    }

    /**
     * Bulk status update for multiple suggestions
     */
    public BulkUpdateResult bulkUpdateStatus(Long[] suggestionIds, SuggestionStatus newStatus, 
                                           String adminToken, String reason) {
        if (!isAdmin(adminToken)) {
            throw new SecurityException("Admin privileges required");
        }
        
        String adminName = getAdminName(adminToken);
        int successCount = 0;
        int failureCount = 0;
        
        for (Long suggestionId : suggestionIds) {
            try {
                suggestionService.changeStatus(suggestionId, newStatus, adminName, reason);
                successCount++;
            } catch (Exception e) {
                failureCount++;
            }
        }
        
        return new BulkUpdateResult(successCount, failureCount);
    }

    /**
     * Initialize sample data for demo purposes
     */
    public void initializeSampleData() {
        // Create sample employees first
        employeeService.createSampleEmployees();
        
        // Then create sample suggestions
        suggestionService.createSampleSuggestions();
        
        // Add some sample votes (would need device identifiers in real scenario)
        // This is for demo purposes only
    }

    /**
     * System maintenance operations
     */
    public MaintenanceResult performMaintenance(String adminToken) {
        if (!isAdmin(adminToken)) {
            throw new SecurityException("Admin privileges required");
        }
        
        MaintenanceResult result = new MaintenanceResult();
        result.startTime = LocalDateTime.now();
        
        try {
            // Cleanup orphaned votes
            result.orphanedVotesRemoved = voteService.cleanupOrphanedVotes();
            
            // Additional cleanup operations can be added here
            result.success = true;
            result.message = "Maintenance completed successfully";
            
        } catch (Exception e) {
            result.success = false;
            result.message = "Maintenance failed: " + e.getMessage();
        }
        
        result.endTime = LocalDateTime.now();
        return result;
    }

    /**
     * Export suggestions data (placeholder for actual export functionality)
     */
    public ExportResult exportSuggestions(String format, String adminToken) {
        if (!isAdmin(adminToken)) {
            throw new SecurityException("Admin privileges required");
        }
        
        // Placeholder implementation
        ExportResult result = new ExportResult();
        result.format = format;
        result.recordCount = (int) suggestionService.countTotalSuggestions();
        result.success = true;
        result.downloadUrl = "/api/admin/export/suggestions_" + System.currentTimeMillis() + "." + format;
        
        return result;
    }

    /**
     * Get system health check
     */
    @Transactional(readOnly = true)
    public SystemHealthCheck getSystemHealth() {
        SystemHealthCheck health = new SystemHealthCheck();
        
        try {
            health.databaseConnected = suggestionService.countTotalSuggestions() >= 0;
            health.totalRecords = suggestionService.countTotalSuggestions() + 
                                 employeeService.getTotalEmployeeCount() + 
                                 voteService.getTotalVoteCount();
            health.lastUpdated = LocalDateTime.now();
            health.status = "HEALTHY";
        } catch (Exception e) {
            health.databaseConnected = false;
            health.status = "ERROR";
            health.errorMessage = e.getMessage();
        }
        
        return health;
    }

    /**
     * Get admin name from token (demo implementation)
     */
    private String getAdminName(String adminToken) {
        // In production, this would decode the actual admin token
        return "admin123".equals(adminToken) ? "Administrator" : "Admin";
    }

    public List<Suggestion> getDeletedSuggestions() {
        return suggestionService.findByDeletedTrue();
    }

    public void hardDeleteSuggestions() {
        suggestionService.hardDeleteSuggestions();
    }

    // Inner classes for return types
    public static class DashboardStatistics {
        public long totalSuggestions;
        public long anonymousSuggestions;
        public long openSuggestions;
        public long underReviewSuggestions;
        public long implementedSuggestions;
        public long rejectedSuggestions;
        public long totalVotes;
        public long uniqueVoters;
        public double averageVotesPerSuggestion;
        public int recentSuggestions7Days;
        public int recentVotes7Days;
        public int recentStatusChanges7Days;
        public long totalEmployees;
        public Map<SuggestionStatus, Long> statusChangeStatistics;
        public Map<String, Long> adminActivityStatistics;
    }

    public static class BulkUpdateResult {
        public final int successCount;
        public final int failureCount;
        public final int totalCount;

        public BulkUpdateResult(int successCount, int failureCount) {
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.totalCount = successCount + failureCount;
        }

        public boolean isFullySuccessful() {
            return failureCount == 0;
        }

        public double getSuccessRate() {
            return totalCount > 0 ? (double) successCount / totalCount * 100 : 0;
        }
    }

    public static class MaintenanceResult {
        public LocalDateTime startTime;
        public LocalDateTime endTime;
        public boolean success;
        public String message;
        public int orphanedVotesRemoved;

        public long getDurationSeconds() {
            if (startTime != null && endTime != null) {
                return java.time.Duration.between(startTime, endTime).toSeconds();
            }
            return 0;
        }
    }

    public static class ExportResult {
        public String format;
        public int recordCount;
        public boolean success;
        public String downloadUrl;
        public String errorMessage;
    }

    public static class SystemHealthCheck {
        public boolean databaseConnected;
        public long totalRecords;
        public LocalDateTime lastUpdated;
        public String status;
        public String errorMessage;
    }
}