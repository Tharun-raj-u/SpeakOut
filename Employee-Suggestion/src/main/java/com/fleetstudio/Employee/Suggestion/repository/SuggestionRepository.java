package com.fleetstudio.Employee.Suggestion.repository;

import com.fleetstudio.Employee.Suggestion.model.Employee;
import com.fleetstudio.Employee.Suggestion.model.Suggestion;
import com.fleetstudio.Employee.Suggestion.model.SuggestionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, Long> {
    
    /**
     * Find all non-deleted suggestions ordered by creation date (newest first)
     */
    List<Suggestion> findByDeletedFalseOrderByCreatedAtDesc();
    
    /**
     * Find all non-deleted suggestions with pagination
     */
    Page<Suggestion> findByDeletedFalseOrderByCreatedAtDesc(Pageable pageable);
    
    /**
     * Find non-deleted suggestion by ID
     */
    Optional<Suggestion> findByIdAndDeletedFalse(Long id);
    
    /**
     * Find suggestions by status (excluding deleted)
     */
    List<Suggestion> findByStatusAndDeletedFalseOrderByCreatedAtDesc(SuggestionStatus status);
    
    /**
     * Find suggestions by status with pagination
     */
    Page<Suggestion> findByStatusAndDeletedFalseOrderByCreatedAtDesc(SuggestionStatus status, Pageable pageable);
    
    /**
     * Find suggestions by employee (excluding deleted)
     */
    List<Suggestion> findBySubmittedByAndDeletedFalseOrderByCreatedAtDesc(Employee employee);
    
    /**
     * Find anonymous suggestions (excluding deleted)
     */
    List<Suggestion> findByIsAnonymousTrueAndDeletedFalseOrderByCreatedAtDesc();
    
    /**
     * Find suggestions by title containing search term (case insensitive, excluding deleted)
     */
    List<Suggestion> findByTitleContainingIgnoreCaseAndDeletedFalseOrderByCreatedAtDesc(String titleSearch);
    
    /**
     * Find suggestions by description containing search term (case insensitive, excluding deleted)
     */
    List<Suggestion> findByDescriptionContainingIgnoreCaseAndDeletedFalseOrderByCreatedAtDesc(String descriptionSearch);
    
    /**
     * Search suggestions by title or description
     */
    @Query("SELECT s FROM Suggestion s WHERE s.deleted = false AND " +
           "(LOWER(s.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(s.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY s.createdAt DESC")
    List<Suggestion> searchByTitleOrDescription(@Param("searchTerm") String searchTerm);
    
    /**
     * Find top suggestions by vote count (excluding deleted)
     */
    @Query("SELECT s FROM Suggestion s WHERE s.deleted = false " +
           "ORDER BY s.voteCount DESC, s.createdAt DESC")
    Page<Suggestion> findTopByVoteCount(Pageable pageable);
    
    /**
     * Find recent suggestions (last N days, excluding deleted)
     */
    @Query("SELECT s FROM Suggestion s WHERE s.deleted = false " +
           "AND s.createdAt >= :sinceDate ORDER BY s.createdAt DESC")
    List<Suggestion> findRecentSuggestions(@Param("sinceDate") LocalDateTime sinceDate);
    
    /**
     * Count suggestions by status (excluding deleted)
     */
    long countByStatusAndDeletedFalse(SuggestionStatus status);
    
    /**
     * Count total non-deleted suggestions
     */
    long countByDeletedFalse();
    
    /**
     * Count anonymous suggestions (excluding deleted)
     */
    long countByIsAnonymousTrueAndDeletedFalse();
    
    /**
     * Count suggestions by employee (excluding deleted)
     */
    long countBySubmittedByAndDeletedFalse(Employee employee);
    
    /**
     * Get suggestions with vote count greater than threshold
     */
    @Query("SELECT s FROM Suggestion s WHERE s.deleted = false " +
           "AND s.voteCount >= :minVotes ORDER BY s.voteCount DESC, s.createdAt DESC")
    List<Suggestion> findByVoteCountGreaterThanEqual(@Param("minVotes") Integer minVotes);
    
    /**
     * Get statistics for dashboard
     */
    @Query("SELECT s.status, COUNT(s) FROM Suggestion s WHERE s.deleted = false GROUP BY s.status")
    List<Object[]> getStatusStatistics();
    
    /**
     * Soft delete suggestion by ID
     */
    @Modifying
    @Query("UPDATE Suggestion s SET s.deleted = true, s.deletedAt = :deleteTime " +
           "WHERE s.id = :suggestionId AND s.deleted = false")
    int softDeleteById(@Param("suggestionId") Long suggestionId, @Param("deleteTime") LocalDateTime deleteTime);
    
    /**
     * Update suggestion status
     */
    @Modifying
    @Query("UPDATE Suggestion s SET s.status = :status, s.updatedAt = :updateTime " +
           "WHERE s.id = :suggestionId AND s.deleted = false")
    int updateStatusById(@Param("suggestionId") Long suggestionId, 
                        @Param("status") SuggestionStatus status, 
                        @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * Increment vote count
     */
    @Modifying
    @Query("UPDATE Suggestion s SET s.voteCount = s.voteCount + 1, s.updatedAt = :updateTime " +
           "WHERE s.id = :suggestionId AND s.deleted = false")
    int incrementVoteCount(@Param("suggestionId") Long suggestionId, @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * Decrement vote count (ensuring it doesn't go below 0)
     */
    @Modifying
    @Query("UPDATE Suggestion s SET s.voteCount = CASE WHEN s.voteCount > 0 THEN s.voteCount - 1 ELSE 0 END, " +
           "s.updatedAt = :updateTime WHERE s.id = :suggestionId AND s.deleted = false")
    int decrementVoteCount(@Param("suggestionId") Long suggestionId, @Param("updateTime") LocalDateTime updateTime);

    List<Suggestion> findByDeletedTrue();
    void deleteByDeleted(boolean deleted);
}