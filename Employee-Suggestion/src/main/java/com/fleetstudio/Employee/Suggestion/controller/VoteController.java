package com.fleetstudio.Employee.Suggestion.controller;

import com.fleetstudio.Employee.Suggestion.model.Vote;
import com.fleetstudio.Employee.Suggestion.security.jwt.UserDetailsImpl;
import com.fleetstudio.Employee.Suggestion.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/votes")
@CrossOrigin(origins = "*")
public class VoteController {

    private final VoteService voteService;

    @Autowired
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    /**
     * Vote for a suggestion
     */


    /**
     * Remove vote for a suggestion
     */
    @DeleteMapping("/suggestion/{suggestionId}")
    public ResponseEntity<VoteResponse> removeVoteForSuggestion(
            @PathVariable Long suggestionId,
            @RequestBody VoteRequest request,
            HttpServletRequest httpRequest) {
        
        try {
            String deviceId = getDeviceIdentifier(request.getDeviceId(), httpRequest);
            boolean success = voteService.removeVoteForSuggestion(suggestionId, deviceId);
            
            long newVoteCount = voteService.getVoteCount(suggestionId);
            
            if (success) {
                return ResponseEntity.ok(new VoteResponse(false, true, newVoteCount, "Vote removed successfully"));
            } else {
                return ResponseEntity.badRequest().body(new VoteResponse(false, false, newVoteCount, "No vote to remove"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new VoteResponse(false, false, 0, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new VoteResponse(false, false, 0, "Failed to remove vote"));
        }
    }

    /**
     * Toggle vote for a suggestion (vote if not voted, unvote if already voted)

     * Check if user has voted for a suggestion
     */
        @PostMapping("/suggestion/{suggestionId}/toggle")
        @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
        public ResponseEntity<VoteResponse> toggleVote(
                @PathVariable Long suggestionId,
                @RequestBody VoteRequest request,
                @AuthenticationPrincipal UserDetailsImpl userDetails,
                HttpServletRequest httpRequest) {

            try {
                Long employeeId = userDetails.getId(); // ✅ taken from token

                String deviceId = getDeviceIdentifier(request.getDeviceId(), httpRequest);

                VoteService.VoteResult result = voteService.toggleVote(
                        suggestionId,
                        deviceId,
                        employeeId
                );

                return ResponseEntity.ok(new VoteResponse(
                        result.isVoted(),
                        result.isSuccess(),
                        result.getNewVoteCount(),
                        result.getMessage()
                ));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(new VoteResponse(false, false, 0, e.getMessage()));
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body(new VoteResponse(false, false, 0, "Failed to toggle vote"));
            }
        }

        // Example implementation (you already have this I think)
        private String getDeviceIdentifier(String deviceIdFromReq, HttpServletRequest httpRequest) {
            if (deviceIdFromReq != null && !deviceIdFromReq.isEmpty()) {
                return deviceIdFromReq;
            }
            return httpRequest.getRemoteAddr(); // fallback to IP
        }
    @GetMapping("/suggestion/{suggestionId}/status")
    public ResponseEntity<VoteStatusResponse> getVoteStatus(
            @PathVariable Long suggestionId,
            @RequestParam(required = false) String deviceId,
            HttpServletRequest httpRequest) {
        
        try {
            String actualDeviceId = getDeviceIdentifier(deviceId, httpRequest);
            boolean hasVoted = voteService.hasVoted(suggestionId, actualDeviceId);
            long voteCount = voteService.getVoteCount(suggestionId);
            
            return ResponseEntity.ok(new VoteStatusResponse(hasVoted, voteCount));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new VoteStatusResponse(false, 0));
        }
    }

    /**
     * Get vote count for a suggestion
     */
    @GetMapping("/suggestion/{suggestionId}/count")
    public ResponseEntity<VoteCountResponse> getVoteCount(@PathVariable Long suggestionId) {
        try {
            long count = voteService.getVoteCount(suggestionId);
            return ResponseEntity.ok(new VoteCountResponse(count));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new VoteCountResponse(0));
        }
    }

    /**
     * Get all votes for a suggestion
     */
    @GetMapping("/suggestion/{suggestionId}")
    public ResponseEntity<List<Vote>> getVotesForSuggestion(@PathVariable Long suggestionId) {
        try {
            List<Vote> votes = voteService.getVotesForSuggestion(suggestionId);
            return ResponseEntity.ok(votes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get voting history for current device
     */
    @GetMapping("/history")
    public ResponseEntity<List<Vote>> getVotingHistory(
            @RequestParam(required = false) String deviceId,
            HttpServletRequest httpRequest) {
        
        try {
            String actualDeviceId = getDeviceIdentifier(deviceId, httpRequest);
            List<Vote> votes = voteService.getVotingHistoryByDevice(actualDeviceId);
            return ResponseEntity.ok(votes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get voting history for an employee
     */
    @GetMapping("/employee/{employeeId}/history")
    public ResponseEntity<List<Vote>> getEmployeeVotingHistory(@PathVariable Long employeeId) {
        try {
            List<Vote> votes = voteService.getVotingHistoryByEmployee(employeeId);
            return ResponseEntity.ok(votes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get recent votes
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Vote>> getRecentVotes(@RequestParam(defaultValue = "7") int days) {
        List<Vote> votes = voteService.getRecentVotes(days);
        return ResponseEntity.ok(votes);
    }

    /**
     * Get top voted suggestions
     */
    @GetMapping("/top-suggestions")
    public ResponseEntity<List<Object[]>> getTopVotedSuggestions() {
        List<Object[]> topSuggestions = voteService.getTopVotedSuggestions();
        return ResponseEntity.ok(topSuggestions);
    }

    /**
     * Get voting statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<VotingStatistics> getVotingStatistics() {
        Map<String, Object> stats = voteService.getVotingEngagementStatistics();
        
        VotingStatistics votingStats = new VotingStatistics();
        votingStats.totalVotes = (Long) stats.get("totalVotes");
        votingStats.uniqueVoters = (Long) stats.get("uniqueVoters");
        votingStats.averageVotesPerSuggestion = (Double) stats.get("averageVotesPerSuggestion");
        votingStats.recentVotes7Days = (Integer) stats.get("recentVotes7Days");
        votingStats.recentVotes30Days = (Integer) stats.get("recentVotes30Days");
        
        return ResponseEntity.ok(votingStats);
    }

    /**
     * Get voting statistics by date
     */
    @GetMapping("/statistics/by-date")
    public ResponseEntity<List<Object[]>> getVotingStatisticsByDate(
            @RequestParam(defaultValue = "30") int days) {
        List<Object[]> statistics = voteService.getVotingStatisticsByDate(days);
        return ResponseEntity.ok(statistics);
    }

    /**
     * Generate a device identifier for testing purposes
     */
    @GetMapping("/device-id")
    public ResponseEntity<DeviceIdResponse> generateDeviceId() {
        String deviceId = voteService.generateDeviceIdentifier();
        return ResponseEntity.ok(new DeviceIdResponse(deviceId));
    }

    /**
     * Get or generate device identifier
     */


    // Request/Response classes
    public static class VoteRequest {
        private String deviceId;


        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        

    }

    public static class VoteResponse {
        private boolean voted;
        private boolean success;
        private long voteCount;
        private String message;

        public VoteResponse(boolean voted, boolean success, long voteCount, String message) {
            this.voted = voted;
            this.success = success;
            this.voteCount = voteCount;
            this.message = message;
        }

        public boolean isVoted() { return voted; }
        public boolean isSuccess() { return success; }
        public long getVoteCount() { return voteCount; }
        public String getMessage() { return message; }
    }

    public static class VoteStatusResponse {
        private boolean hasVoted;
        private long voteCount;

        public VoteStatusResponse(boolean hasVoted, long voteCount) {
            this.hasVoted = hasVoted;
            this.voteCount = voteCount;
        }

        public boolean isHasVoted() { return hasVoted; }
        public long getVoteCount() { return voteCount; }
    }

    public static class VoteCountResponse {
        private long count;

        public VoteCountResponse(long count) {
            this.count = count;
        }

        public long getCount() { return count; }
    }

    public static class VotingStatistics {
        public long totalVotes;
        public long uniqueVoters;
        public double averageVotesPerSuggestion;
        public int recentVotes7Days;
        public int recentVotes30Days;
    }

    public static class DeviceIdResponse {
        private String deviceId;

        public DeviceIdResponse(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceId() { return deviceId; }
    }
}