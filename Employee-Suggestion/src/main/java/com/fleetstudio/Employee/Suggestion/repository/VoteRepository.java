package com.fleetstudio.Employee.Suggestion.repository;

import com.fleetstudio.Employee.Suggestion.model.Suggestion;
import com.fleetstudio.Employee.Suggestion.model.Vote;
import com.fleetstudio.Employee.Suggestion.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    
    /**
     * Check if a device has already voted for a specific suggestion
     */
    boolean existsBySuggestionAndDeviceIdentifier(Suggestion suggestion, String deviceIdentifier);
    
    /**
     * Check if a device has already voted for a suggestion by ID
     */
    @Query("SELECT COUNT(v) > 0 FROM Vote v WHERE v.suggestion.id = :suggestionId AND v.deviceIdentifier = :deviceIdentifier")
    boolean existsBySuggestionIdAndDeviceIdentifier(@Param("suggestionId") Long suggestionId, 
                                                   @Param("deviceIdentifier") String deviceIdentifier);
    
    /**
     * Find vote by suggestion and device identifier
     */
    Optional<Vote> findBySuggestionAndDeviceIdentifier(Suggestion suggestion, String deviceIdentifier);
    
    /**
     * Find vote by suggestion ID and device identifier
     */
    @Query("SELECT v FROM Vote v WHERE v.suggestion.id = :suggestionId AND v.deviceIdentifier = :deviceIdentifier")
    Optional<Vote> findBySuggestionIdAndDeviceIdentifier(@Param("suggestionId") Long suggestionId, 
                                                        @Param("deviceIdentifier") String deviceIdentifier);
    
    /**
     * Find all votes for a specific suggestion
     */
    List<Vote> findBySuggestionOrderByCreatedAtDesc(Suggestion suggestion);
    
    /**
     * Find all votes for a suggestion by ID
     */
    @Query("SELECT v FROM Vote v WHERE v.suggestion.id = :suggestionId ORDER BY v.createdAt DESC")
    List<Vote> findBySuggestionIdOrderByCreatedAtDesc(@Param("suggestionId") Long suggestionId);
    
    /**
     * Count votes for a specific suggestion
     */
    long countBySuggestion(Suggestion suggestion);
    
    /**
     * Count votes for a suggestion by ID
     */
    @Query("SELECT COUNT(v) FROM Vote v WHERE v.suggestion.id = :suggestionId")
    long countBySuggestionId(@Param("suggestionId") Long suggestionId);
    
    /**
     * Find all votes by a specific employee
     */
    List<Vote> findByEmployeeOrderByCreatedAtDesc(Employee employee);
    
    /**
     * Find all votes by device identifier (to track voting history of a device)
     */
    List<Vote> findByDeviceIdentifierOrderByCreatedAtDesc(String deviceIdentifier);
    
    /**
     * Find votes within a date range
     */
    @Query("SELECT v FROM Vote v WHERE v.createdAt BETWEEN :startDate AND :endDate ORDER BY v.createdAt DESC")
    List<Vote> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate, 
                                     @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get voting statistics by date
     */
    @Query("SELECT DATE(v.createdAt), COUNT(v) FROM Vote v " +
           "WHERE v.createdAt >= :sinceDate " +
           "GROUP BY DATE(v.createdAt) ORDER BY DATE(v.createdAt) DESC")
    List<Object[]> getVotingStatisticsByDate(@Param("sinceDate") LocalDateTime sinceDate);
    
    /**
     * Get top voted suggestions with vote counts
     */
    @Query("SELECT v.suggestion, COUNT(v) as voteCount FROM Vote v " +
           "WHERE v.suggestion.deleted = false " +
           "GROUP BY v.suggestion ORDER BY voteCount DESC")
    List<Object[]> getTopVotedSuggestions();
    
    /**
     * Find votes for non-deleted suggestions only
     */
    @Query("SELECT v FROM Vote v WHERE v.suggestion.deleted = false ORDER BY v.createdAt DESC")
    List<Vote> findVotesForActiveSuggestions();
    
    /**
     * Count unique voters (by device identifier)
     */
    @Query("SELECT COUNT(DISTINCT v.deviceIdentifier) FROM Vote v")
    long countUniqueVoters();
    
    /**
     * Count unique voters for a specific suggestion
     */
    @Query("SELECT COUNT(DISTINCT v.deviceIdentifier) FROM Vote v WHERE v.suggestion.id = :suggestionId")
    long countUniqueVotersBySuggestionId(@Param("suggestionId") Long suggestionId);
    
    /**
     * Get recent votes (last N days)
     */
    @Query("SELECT v FROM Vote v WHERE v.createdAt >= :sinceDate ORDER BY v.createdAt DESC")
    List<Vote> findRecentVotes(@Param("sinceDate") LocalDateTime sinceDate);
    
    /**
     * Delete all votes for a specific suggestion (cleanup when suggestion is hard deleted)
     */
    void deleteBySuggestion(Suggestion suggestion);
    
    /**
     * Delete vote by suggestion ID and device identifier (for unvoting functionality)
     */
    @Query("DELETE FROM Vote v WHERE v.suggestion.id = :suggestionId AND v.deviceIdentifier = :deviceIdentifier")
    int deleteBySuggestionIdAndDeviceIdentifier(@Param("suggestionId") Long suggestionId, 
                                               @Param("deviceIdentifier") String deviceIdentifier);
}