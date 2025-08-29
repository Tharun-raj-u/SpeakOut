package com.fleetstudio.Employee.Suggestion.service;


import com.fleetstudio.Employee.Suggestion.model.Employee;

import com.fleetstudio.Employee.Suggestion.model.Suggestion;
import com.fleetstudio.Employee.Suggestion.model.SuggestionStatus;
import com.fleetstudio.Employee.Suggestion.model.SuggestionStatusHistory;
import com.fleetstudio.Employee.Suggestion.repository.EmployeeRepository;
import com.fleetstudio.Employee.Suggestion.repository.SuggestionRepository;
import com.fleetstudio.Employee.Suggestion.repository.SuggestionStatusHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class SuggestionService {

    private final SuggestionRepository suggestionRepository;
    private final EmployeeRepository employeeRepository;
    private final SuggestionStatusHistoryRepository statusHistoryRepository;

    @Autowired
    public SuggestionService(SuggestionRepository suggestionRepository,
                           EmployeeRepository employeeRepository,
                           SuggestionStatusHistoryRepository statusHistoryRepository) {
        this.suggestionRepository = suggestionRepository;
        this.employeeRepository = employeeRepository;
        this.statusHistoryRepository = statusHistoryRepository;
    }

    /**
     * Get all suggestions (non-deleted) ordered by creation date
     */
    @Transactional(readOnly = true)
    public List<Suggestion> getAllSuggestions() {
        return suggestionRepository.findByDeletedFalseOrderByCreatedAtDesc();
    }

    /**
     * Get suggestions with pagination
     */
    @Transactional(readOnly = true)
    public Page<Suggestion> getAllSuggestions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return suggestionRepository.findByDeletedFalseOrderByCreatedAtDesc(pageable);
    }

    /**
     * Get suggestion by ID (non-deleted only)
     */
    @Transactional(readOnly = true)
    public Optional<Suggestion> getSuggestionById(Long id) {
        return suggestionRepository.findByIdAndDeletedFalse(id);
    }

    /**
     * Create a new suggestion
     */
    public Suggestion createSuggestion(String title, String description, Long employeeId, boolean isAnonymous) {

        validateSuggestionData(title, description);

        Employee submittedBy = null;
        if (!isAnonymous && employeeId != null) {
            submittedBy = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee with ID " + employeeId + " not found"));
        }

        Suggestion suggestion = new Suggestion(title, description, submittedBy, isAnonymous);
        Suggestion savedSuggestion = suggestionRepository.save(suggestion);

        // Create initial status history entry
        createStatusHistoryEntry(savedSuggestion, null, SuggestionStatus.OPEN, "System");

        return savedSuggestion;
    }

    /**
     * Update suggestion (only title and description can be updated)
     */
    public Suggestion updateSuggestion(Long id, String title, String description) {
        validateSuggestionData(title, description);

        Suggestion suggestion = suggestionRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new IllegalArgumentException("Suggestion with ID " + id + " not found"));

        suggestion.setTitle(title);
        suggestion.setDescription(description);

        return suggestionRepository.save(suggestion);
    }

    /**
     * Change suggestion status (Admin only)
     */
    public Suggestion changeStatus(Long id, SuggestionStatus newStatus, String adminName, String reason) {
        Suggestion suggestion = suggestionRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new IllegalArgumentException("Suggestion with ID " + id + " not found"));

        SuggestionStatus previousStatus = suggestion.getStatus();
        
        if (previousStatus == newStatus) {
            throw new IllegalArgumentException("Suggestion is already in " + newStatus.getDisplayName() + " status");
        }

        suggestion.setStatus(newStatus);
        Suggestion updatedSuggestion = suggestionRepository.save(suggestion);

        // Create status history entry
        createStatusHistoryEntry(updatedSuggestion, previousStatus, newStatus, adminName, reason);

        return updatedSuggestion;
    }

    /**
     * Soft delete suggestion (Admin only)
     */
    public void deleteSuggestion(Long id, String adminName) {
        Suggestion suggestion = suggestionRepository.findByIdAndDeletedFalse(id)
            .orElseThrow(() -> new IllegalArgumentException("Suggestion with ID " + id + " not found"));

        suggestion.softDelete();
        suggestionRepository.save(suggestion);

        // Create status history entry for deletion
        createStatusHistoryEntry(suggestion, suggestion.getStatus(), suggestion.getStatus(), 
                                adminName, "Suggestion deleted by admin");
    }

    /**
     * Get suggestions by status
     */
    @Transactional(readOnly = true)
    public List<Suggestion> getSuggestionsByStatus(SuggestionStatus status) {
        return suggestionRepository.findByStatusAndDeletedFalseOrderByCreatedAtDesc(status);
    }

    /**
     * Get suggestions by status with pagination
     */
    @Transactional(readOnly = true)
    public Page<Suggestion> getSuggestionsByStatus(SuggestionStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return suggestionRepository.findByStatusAndDeletedFalseOrderByCreatedAtDesc(status, pageable);
    }

    /**
     * Get suggestions by employee
     */
    @Transactional(readOnly = true)
    public List<Suggestion> getSuggestionsByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("Employee with ID " + employeeId + " not found"));
        
        return suggestionRepository.findBySubmittedByAndDeletedFalseOrderByCreatedAtDesc(employee);
    }

    /**
     * Get anonymous suggestions
     */
    @Transactional(readOnly = true)
    public List<Suggestion> getAnonymousSuggestions() {
        return suggestionRepository.findByIsAnonymousTrueAndDeletedFalseOrderByCreatedAtDesc();
    }

    /**
     * Search suggestions by title or description
     */
    @Transactional(readOnly = true)
    public List<Suggestion> searchSuggestions(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllSuggestions();
        }
        return suggestionRepository.searchByTitleOrDescription(searchTerm.trim());
    }

    /**
     * Get top suggestions by vote count
     */
    @Transactional(readOnly = true)
    public Page<Suggestion> getTopSuggestions(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return suggestionRepository.findTopByVoteCount(pageable);
    }

    /**
     * Get recent suggestions (last N days)
     */
    @Transactional(readOnly = true)
    public List<Suggestion> getRecentSuggestions(int days) {
        LocalDateTime sinceDate = LocalDateTime.now().minusDays(days);
        return suggestionRepository.findRecentSuggestions(sinceDate);
    }

    /**
     * Get suggestions with high vote count
     */
    @Transactional(readOnly = true)
    public List<Suggestion> getPopularSuggestions(int minVotes) {
        return suggestionRepository.findByVoteCountGreaterThanEqual(minVotes);
    }

    /**
     * Get suggestion statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Long> getSuggestionStatistics() {
        List<Object[]> statusStats = suggestionRepository.getStatusStatistics();
        
        Map<String, Long> statistics = new java.util.HashMap<>();
        statistics.put("total", countTotalSuggestions());
        statistics.put("anonymous", countAnonymousSuggestions());
        
        for (Object[] stat : statusStats) {
            SuggestionStatus status = (SuggestionStatus) stat[0];
            Long count = (Long) stat[1];
            statistics.put(status.name().toLowerCase(), count);
        }
        
        return statistics;
    }

    /**
     * Count total suggestions
     */
    @Transactional(readOnly = true)
    public long countTotalSuggestions() {
        return suggestionRepository.countByDeletedFalse();
    }

    /**
     * Count suggestions by status
     */
    @Transactional(readOnly = true)
    public long countSuggestionsByStatus(SuggestionStatus status) {
        return suggestionRepository.countByStatusAndDeletedFalse(status);
    }

    /**
     * Count anonymous suggestions
     */
    @Transactional(readOnly = true)
    public long countAnonymousSuggestions() {
        return suggestionRepository.countByIsAnonymousTrueAndDeletedFalse();
    }

    /**
     * Count suggestions by employee
     */
    @Transactional(readOnly = true)
    public long countSuggestionsByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("Employee with ID " + employeeId + " not found"));
        
        return suggestionRepository.countBySubmittedByAndDeletedFalse(employee);
    }

    /**
     * Get suggestion status history
     */
    @Transactional(readOnly = true)
    public List<SuggestionStatusHistory> getSuggestionStatusHistory(Long suggestionId) {
        return statusHistoryRepository.findBySuggestionIdOrderByCreatedAtDesc(suggestionId);
    }

    /**
     * Create sample suggestions for demo purposes
     */
    public void createSampleSuggestions() {
        if (suggestionRepository.count() == 0) {
            List<Employee> employees = employeeRepository.findAll();
            
            if (!employees.isEmpty()) {
                // Create some sample suggestions
                createSuggestion("Flexible Work Hours", 
                    "Allow employees to work flexible hours to improve work-life balance and productivity.", 
                    employees.get(0).getId(), false);
                
                createSuggestion("Coffee Station Improvement", 
                    "Upgrade the office coffee station with better quality coffee and more variety.", 
                    employees.get(1).getId(), false);
                
                createSuggestion("Remote Work Policy", 
                    "Implement a clear remote work policy that allows working from home 2-3 days per week.", 
                    null, true); // Anonymous
                
                createSuggestion("Team Building Activities", 
                    "Organize monthly team building activities to improve collaboration and team spirit.", 
                    employees.get(2).getId(), false);
                
                createSuggestion("Learning Budget", 
                    "Provide annual learning budget for employees to attend conferences or take online courses.", 
                    null, true); // Anonymous
            }
        }
    }

    /**
     * Create status history entry
     */
    private void createStatusHistoryEntry(Suggestion suggestion, SuggestionStatus previousStatus, 
                                        SuggestionStatus newStatus, String changedBy) {
        createStatusHistoryEntry(suggestion, previousStatus, newStatus, changedBy, null);
    }

    /**
     * Create status history entry with reason
     */
    private void createStatusHistoryEntry(Suggestion suggestion, SuggestionStatus previousStatus, 
                                        SuggestionStatus newStatus, String changedBy, String reason) {
        SuggestionStatusHistory history = new SuggestionStatusHistory(
            suggestion, previousStatus, newStatus, changedBy, reason);
        statusHistoryRepository.save(history);
    }

    /**
     * Validate suggestion data
     */
    private void validateSuggestionData(String title, String description) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Suggestion title is required");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Suggestion description is required");
        }
        if (title.length() > 200) {
            throw new IllegalArgumentException("Suggestion title cannot exceed 200 characters");
        }
    }

    public List<Suggestion> findByDeletedTrue() {
        return suggestionRepository.findByDeletedTrue();

    }

    public void hardDeleteSuggestions() {
         suggestionRepository.deleteByDeleted(true);
    }
}