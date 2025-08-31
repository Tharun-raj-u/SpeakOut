package com.fleetstudio.Employee.Suggestion.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "suggestion_status_history")
public class SuggestionStatusHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "suggestion_id", nullable = false)
    @JsonIgnore
    private Suggestion suggestion;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status")
    private SuggestionStatus previousStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private SuggestionStatus newStatus;
    
    @Column(name = "changed_by", length = 100)
    private String changedBy; // Could be admin username or "System"
    
    @Column(name = "change_reason", length = 500)
    private String changeReason;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public SuggestionStatusHistory() {}
    
    public SuggestionStatusHistory(Suggestion suggestion, SuggestionStatus previousStatus, 
                                 SuggestionStatus newStatus, String changedBy) {
        this.suggestion = suggestion;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.createdAt = LocalDateTime.now();
    }
    
    public SuggestionStatusHistory(Suggestion suggestion, SuggestionStatus previousStatus, 
                                 SuggestionStatus newStatus, String changedBy, String changeReason) {
        this.suggestion = suggestion;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedBy = changedBy;
        this.changeReason = changeReason;
        this.createdAt = LocalDateTime.now();
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Suggestion getSuggestion() {
        return suggestion;
    }
    
    public void setSuggestion(Suggestion suggestion) {
        this.suggestion = suggestion;
    }
    
    public SuggestionStatus getPreviousStatus() {
        return previousStatus;
    }
    
    public void setPreviousStatus(SuggestionStatus previousStatus) {
        this.previousStatus = previousStatus;
    }
    
    public SuggestionStatus getNewStatus() {
        return newStatus;
    }
    
    public void setNewStatus(SuggestionStatus newStatus) {
        this.newStatus = newStatus;
    }
    
    public String getChangedBy() {
        return changedBy;
    }
    
    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }
    
    public String getChangeReason() {
        return changeReason;
    }
    
    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "SuggestionStatusHistory{" +
                "id=" + id +
                ", suggestionId=" + (suggestion != null ? suggestion.getId() : null) +
                ", previousStatus=" + previousStatus +
                ", newStatus=" + newStatus +
                ", changedBy='" + changedBy + '\'' +
                ", changeReason='" + changeReason + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}