package com.fleetstudio.Employee.Suggestion.service;


import com.fleetstudio.Employee.Suggestion.model.Employee;
import com.fleetstudio.Employee.Suggestion.model.Suggestion;
import com.fleetstudio.Employee.Suggestion.model.Vote;
import com.fleetstudio.Employee.Suggestion.repository.EmployeeRepository;
import com.fleetstudio.Employee.Suggestion.repository.SuggestionRepository;
import com.fleetstudio.Employee.Suggestion.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class VoteService {

    private final VoteRepository voteRepository;
    private final SuggestionRepository suggestionRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public VoteService(VoteRepository voteRepository, 
                      SuggestionRepository suggestionRepository,
                      EmployeeRepository employeeRepository) {
        this.voteRepository = voteRepository;
        this.suggestionRepository = suggestionRepository;
        this.employeeRepository = employeeRepository;
    }

    /**
     * Vote for a suggestion
     */
    public boolean voteForSuggestion(Long suggestionId, String deviceIdentifier, Long employeeId) {
        validateDeviceIdentifier(deviceIdentifier);

        // Check if suggestion exists and is not deleted
        Suggestion suggestion = suggestionRepository.findByIdAndDeletedFalse(suggestionId)
            .orElseThrow(() -> new IllegalArgumentException("Suggestion with ID " + suggestionId + " not found or deleted"));

        // Check if device has already voted for this suggestion
        if (voteRepository.existsBySuggestionIdAndDeviceIdentifier(suggestionId, deviceIdentifier)) {
            return false; // Already voted
        }

        // Get employee if provided
        Employee employee = null;
        if (employeeId != null) {
            employee = employeeRepository.findById(employeeId).orElse(null);
        }

        // Create vote
        Vote vote = new Vote(suggestion, deviceIdentifier, employee);
        voteRepository.save(vote);

        // Update suggestion vote count
        suggestion.incrementVoteCount();
        suggestionRepository.save(suggestion);

        return true; // Vote successful
    }

    /**
     * Remove vote for a suggestion (unvote)
     */
    public boolean removeVoteForSuggestion(Long suggestionId, String deviceIdentifier) {
        validateDeviceIdentifier(deviceIdentifier);

        // Check if suggestion exists and is not deleted
        Suggestion suggestion = suggestionRepository.findByIdAndDeletedFalse(suggestionId)
            .orElseThrow(() -> new IllegalArgumentException("Suggestion with ID " + suggestionId + " not found or deleted"));

        // Check if vote exists
        Optional<Vote> existingVote = voteRepository.findBySuggestionIdAndDeviceIdentifier(suggestionId, deviceIdentifier);
        
        if (existingVote.isEmpty()) {
            return false; // No vote to remove
        }

        // Remove vote
        voteRepository.delete(existingVote.get());

        // Update suggestion vote count
        suggestion.decrementVoteCount();
        suggestionRepository.save(suggestion);

        return true; // Unvote successful
    }

    /**
     * Check if a device has already voted for a suggestion
     */
    @Transactional(readOnly = true)
    public boolean hasVoted(Long suggestionId, String deviceIdentifier) {
        validateDeviceIdentifier(deviceIdentifier);
        return voteRepository.existsBySuggestionIdAndDeviceIdentifier(suggestionId, deviceIdentifier);
    }

    /**
     * Get vote count for a suggestion
     */
    @Transactional(readOnly = true)
    public long getVoteCount(Long suggestionId) {
        return voteRepository.countBySuggestionId(suggestionId);
    }

    /**
     * Get all votes for a suggestion
     */
    @Transactional(readOnly = true)
    public List<Vote> getVotesForSuggestion(Long suggestionId) {
        return voteRepository.findBySuggestionIdOrderByCreatedAtDesc(suggestionId);
    }

    /**
     * Get voting history for a device
     */
    @Transactional(readOnly = true)
    public List<Vote> getVotingHistoryByDevice(String deviceIdentifier) {
        validateDeviceIdentifier(deviceIdentifier);
        return voteRepository.findByDeviceIdentifierOrderByCreatedAtDesc(deviceIdentifier);
    }

    /**
     * Get voting history for an employee
     */
    @Transactional(readOnly = true)
    public List<Vote> getVotingHistoryByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("Employee with ID " + employeeId + " not found"));
        
        return voteRepository.findByEmployeeOrderByCreatedAtDesc(employee);
    }

    /**
     * Get recent votes (last N days)
     */
    @Transactional(readOnly = true)
    public List<Vote> getRecentVotes(int days) {
        LocalDateTime sinceDate = LocalDateTime.now().minusDays(days);
        return voteRepository.findRecentVotes(sinceDate);
    }

    /**
     * Get voting statistics by date
     */
    @Transactional(readOnly = true)
    public List<Object[]> getVotingStatisticsByDate(int days) {
        LocalDateTime sinceDate = LocalDateTime.now().minusDays(days);
        return voteRepository.getVotingStatisticsByDate(sinceDate);
    }

    /**
     * Get top voted suggestions
     */
    @Transactional(readOnly = true)
    public List<Object[]> getTopVotedSuggestions() {
        return voteRepository.getTopVotedSuggestions();
    }

    /**
     * Get total vote count
     */
    @Transactional(readOnly = true)
    public long getTotalVoteCount() {
        return voteRepository.count();
    }

    /**
     * Get unique voter count
     */
    @Transactional(readOnly = true)
    public long getUniqueVoterCount() {
        return voteRepository.countUniqueVoters();
    }

    /**
     * Get unique voters for a specific suggestion
     */
    @Transactional(readOnly = true)
    public long getUniqueVotersForSuggestion(Long suggestionId) {
        return voteRepository.countUniqueVotersBySuggestionId(suggestionId);
    }

    /**
     * Get votes within date range
     */
    @Transactional(readOnly = true)
    public List<Vote> getVotesBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return voteRepository.findByCreatedAtBetween(startDate, endDate);
    }

    /**
     * Get voting engagement statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getVotingEngagementStatistics() {
        Map<String, Object> stats = new java.util.HashMap<>();
        
        stats.put("totalVotes", getTotalVoteCount());
        stats.put("uniqueVoters", getUniqueVoterCount());
        stats.put("recentVotes7Days", getRecentVotes(7).size());
        stats.put("recentVotes30Days", getRecentVotes(30).size());
        
        // Calculate average votes per suggestion
        long totalSuggestions = suggestionRepository.countByDeletedFalse();
        if (totalSuggestions > 0) {
            stats.put("averageVotesPerSuggestion", (double) getTotalVoteCount() / totalSuggestions);
        } else {
            stats.put("averageVotesPerSuggestion", 0.0);
        }
        
        return stats;
    }

    /**
     * Toggle vote for a suggestion (vote if not voted, unvote if already voted)
     */
    public VoteResult toggleVote(Long suggestionId, String deviceIdentifier, Long employeeId) {
        validateDeviceIdentifier(deviceIdentifier);

        boolean hasVoted = hasVoted(suggestionId, deviceIdentifier);
        
        if (hasVoted) {
            boolean removed = removeVoteForSuggestion(suggestionId, deviceIdentifier);
            return new VoteResult(false, removed, getVoteCount(suggestionId));
        } else {
            boolean added = voteForSuggestion(suggestionId, deviceIdentifier, employeeId);
            return new VoteResult(true, added, getVoteCount(suggestionId));
        }
    }

    /**
     * Bulk vote operations for testing or admin purposes
     */
    public void removeAllVotesForSuggestion(Long suggestionId) {
        Suggestion suggestion = suggestionRepository.findByIdAndDeletedFalse(suggestionId)
            .orElseThrow(() -> new IllegalArgumentException("Suggestion with ID " + suggestionId + " not found or deleted"));

        List<Vote> votes = voteRepository.findBySuggestionIdOrderByCreatedAtDesc(suggestionId);
        voteRepository.deleteAll(votes);

        // Reset vote count
        suggestion.setVoteCount(0);
        suggestionRepository.save(suggestion);
    }

    /**
     * Cleanup votes for deleted suggestions (maintenance operation)
     */
    public int cleanupOrphanedVotes() {
        // This would typically be a scheduled task
        List<Vote> allVotes = voteRepository.findAll();
        int cleanedCount = 0;

        for (Vote vote : allVotes) {
            if (vote.getSuggestion().getDeleted()) {
                voteRepository.delete(vote);
                cleanedCount++;
            }
        }

        return cleanedCount;
    }

    /**
     * Generate device identifier if none provided (for testing)
     */
    public String generateDeviceIdentifier() {
        return "device_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000);
    }

    /**
     * Validate device identifier
     */
    private void validateDeviceIdentifier(String deviceIdentifier) {
        if (deviceIdentifier == null || deviceIdentifier.trim().isEmpty()) {
            throw new IllegalArgumentException("Device identifier is required");
        }
        if (deviceIdentifier.length() > 255) {
            throw new IllegalArgumentException("Device identifier cannot exceed 255 characters");
        }
    }

    /**
     * Result class for vote operations
     */
    public static class VoteResult {
        private final boolean voted;
        private final boolean success;
        private final long newVoteCount;

        public VoteResult(boolean voted, boolean success, long newVoteCount) {
            this.voted = voted;
            this.success = success;
            this.newVoteCount = newVoteCount;
        }

        public boolean isVoted() {
            return voted;
        }

        public boolean isSuccess() {
            return success;
        }

        public long getNewVoteCount() {
            return newVoteCount;
        }

        public String getMessage() {
            if (!success) {
                return voted ? "Failed to add vote" : "Failed to remove vote";
            }
            return voted ? "Vote added successfully" : "Vote removed successfully";
        }
    }
}