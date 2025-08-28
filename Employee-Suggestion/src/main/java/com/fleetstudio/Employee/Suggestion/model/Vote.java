package com.fleetstudio.Employee.Suggestion.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "votes", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"suggestion_id", "device_identifier"}))
public class Vote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "suggestion_id", nullable = false)
    @JsonIgnore
    private Suggestion suggestion;
    
    // Using device identifier for anonymous voting (one vote per device per suggestion)
    @Column(name = "device_identifier", nullable = false, length = 255)
    private String deviceIdentifier;
    
    // Optional: If we want to track employee votes (when not anonymous)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    @JsonIgnore
    private Employee employee;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public Vote() {}
    
    public Vote(Suggestion suggestion, String deviceIdentifier) {
        this.suggestion = suggestion;
        this.deviceIdentifier = deviceIdentifier;
        this.createdAt = LocalDateTime.now();
    }
    
    public Vote(Suggestion suggestion, String deviceIdentifier, Employee employee) {
        this.suggestion = suggestion;
        this.deviceIdentifier = deviceIdentifier;
        this.employee = employee;
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
    
    public String getDeviceIdentifier() {
        return deviceIdentifier;
    }
    
    public void setDeviceIdentifier(String deviceIdentifier) {
        this.deviceIdentifier = deviceIdentifier;
    }
    
    public Employee getEmployee() {
        return employee;
    }
    
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                ", suggestionId=" + (suggestion != null ? suggestion.getId() : null) +
                ", deviceIdentifier='" + deviceIdentifier + '\'' +
                ", employeeName=" + (employee != null ? employee.getName() : "Anonymous") +
                ", createdAt=" + createdAt +
                '}';
    }
}