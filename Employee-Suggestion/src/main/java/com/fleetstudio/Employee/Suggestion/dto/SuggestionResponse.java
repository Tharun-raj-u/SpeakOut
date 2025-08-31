package com.fleetstudio.Employee.Suggestion.dto;

import com.fleetstudio.Employee.Suggestion.model.Suggestion;
import com.fleetstudio.Employee.Suggestion.model.SuggestionStatus;

import java.time.LocalDateTime;

public class SuggestionResponse {
    private Long id;
    private String title;
    private String description;
    private Long submitterId;   // 👈 added
    private String submitterName;
    private Boolean isAnonymous;
    private SuggestionStatus status;
    private Integer voteCount;
    private LocalDateTime createdAt;

    public SuggestionResponse(Suggestion s) {
        this.id = s.getId();
        this.title = s.getTitle();
        this.description = s.getDescription();
        this.submitterId = (s.getSubmittedBy() != null) ? s.getSubmittedBy().getId() : null;
        this.submitterName = s.getSubmitterName();
        this.isAnonymous = s.getIsAnonymous();
        this.status = s.getStatus();
        this.voteCount = s.getVoteCount();
        this.createdAt = s.getCreatedAt();
    }

    // Getters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Long getSubmitterId() { return submitterId; }
    public String getSubmitterName() { return submitterName; }
    public Boolean getIsAnonymous() { return isAnonymous; }
    public SuggestionStatus getStatus() { return status; }
    public Integer getVoteCount() { return voteCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
