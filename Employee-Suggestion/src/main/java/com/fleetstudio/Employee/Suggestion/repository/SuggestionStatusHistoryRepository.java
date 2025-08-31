package com.fleetstudio.Employee.Suggestion.repository;


import com.fleetstudio.Employee.Suggestion.model.Suggestion;
import com.fleetstudio.Employee.Suggestion.model.SuggestionStatus;
import com.fleetstudio.Employee.Suggestion.model.SuggestionStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SuggestionStatusHistoryRepository extends JpaRepository<SuggestionStatusHistory, Long> {
    
    /**
     * Find all status history for a specific suggestion ordered by date (newest first)
     */
    List<SuggestionStatusHistory> findBySuggestionOrderByCreatedAtDesc(Suggestion suggestion);
    
    /**
     * Find all status history for a suggestion by ID
     */
    @Query("SELECT sh FROM SuggestionStatusHistory sh WHERE sh.suggestion.id = :suggestionId ORDER BY sh.createdAt DESC")
    List<SuggestionStatusHistory> findBySuggestionIdOrderByCreatedAtDesc(@Param("suggestionId") Long suggestionId);
    
    /**
     * Find the latest status change for a specific suggestion
     */
    @Query("SELECT sh FROM SuggestionStatusHistory sh WHERE sh.suggestion.id = :suggestionId " +
           "ORDER BY sh.createdAt DESC LIMIT 1")
    SuggestionStatusHistory findLatestBySuggestionId(@Param("suggestionId") Long suggestionId);
    
    /**
     * Find status history by changed by (admin/user who made the change)
     */
    List<SuggestionStatusHistory> findByChangedByOrderByCreatedAtDesc(String changedBy);
    
    /**
     * Find status history by new status
     */
    List<SuggestionStatusHistory> findByNewStatusOrderByCreatedAtDesc(SuggestionStatus newStatus);
    
    /**
     * Find status history by previous status
     */
    List<SuggestionStatusHistory> findByPreviousStatusOrderByCreatedAtDesc(SuggestionStatus previousStatus);
    
    /**
     * Find status changes from one specific status to another
     */
    @Query("SELECT sh FROM SuggestionStatusHistory sh WHERE sh.previousStatus = :fromStatus " +
           "AND sh.newStatus = :toStatus ORDER BY sh.createdAt DESC")
    List<SuggestionStatusHistory> findStatusTransitions(@Param("fromStatus") SuggestionStatus fromStatus, 
                                                       @Param("toStatus") SuggestionStatus toStatus);
    
    /**
     * Find status history within a date range
     */
    @Query("SELECT sh FROM SuggestionStatusHistory sh WHERE sh.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY sh.createdAt DESC")
    List<SuggestionStatusHistory> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                                        @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find recent status changes (last N days)
     */
    @Query("SELECT sh FROM SuggestionStatusHistory sh WHERE sh.createdAt >= :sinceDate ORDER BY sh.createdAt DESC")
    List<SuggestionStatusHistory> findRecentStatusChanges(@Param("sinceDate") LocalDateTime sinceDate);
    
    /**
     * Count status changes made by a specific person
     */
    long countByChangedBy(String changedBy);
    
    /**
     * Count status changes to a specific status
     */
    long countByNewStatus(SuggestionStatus newStatus);
    
    /**
     * Get status change statistics (count of changes to each status)
     */
    @Query("SELECT sh.newStatus, COUNT(sh) FROM SuggestionStatusHistory sh GROUP BY sh.newStatus")
    List<Object[]> getStatusChangeStatistics();
    
    /**
     * Get admin activity statistics
     */
    @Query("SELECT sh.changedBy, COUNT(sh) FROM SuggestionStatusHistory sh " +
           "WHERE sh.changedBy IS NOT NULL AND sh.changedBy != 'System' " +
           "GROUP BY sh.changedBy ORDER BY COUNT(sh) DESC")
    List<Object[]> getAdminActivityStatistics();
    
    /**
     * Find suggestions that changed status multiple times
     */
    @Query("SELECT sh.suggestion, COUNT(sh) as changeCount FROM SuggestionStatusHistory sh " +
           "GROUP BY sh.suggestion HAVING COUNT(sh) > 1 ORDER BY changeCount DESC")
    List<Object[]> findSuggestionsWithMultipleStatusChanges();
    
    /**
     * Get status transition analytics (from -> to status counts)
     */
    @Query("SELECT sh.previousStatus, sh.newStatus, COUNT(sh) FROM SuggestionStatusHistory sh " +
           "WHERE sh.previousStatus IS NOT NULL " +
           "GROUP BY sh.previousStatus, sh.newStatus ORDER BY COUNT(sh) DESC")
    List<Object[]> getStatusTransitionAnalytics();
    
    /**
     * Find status history for suggestions that are not deleted
     */
    @Query("SELECT sh FROM SuggestionStatusHistory sh WHERE sh.suggestion.deleted = false ORDER BY sh.createdAt DESC")
    List<SuggestionStatusHistory> findForActiveSuggestions();
    
    /**
     * Get the first status change for each suggestion (when it was moved from initial status)
     */
    @Query("SELECT sh FROM SuggestionStatusHistory sh WHERE sh.id IN (" +
           "SELECT MIN(sh2.id) FROM SuggestionStatusHistory sh2 GROUP BY sh2.suggestion.id" +
           ") ORDER BY sh.createdAt DESC")
    List<SuggestionStatusHistory> findFirstStatusChanges();
    
    /**
     * Delete all status history for a specific suggestion (cleanup when suggestion is hard deleted)
     */
    void deleteBySuggestion(Suggestion suggestion);
    
    /**
     * Count total status changes
     */
    @Query("SELECT COUNT(sh) FROM SuggestionStatusHistory sh")
    long countTotalStatusChanges();
}