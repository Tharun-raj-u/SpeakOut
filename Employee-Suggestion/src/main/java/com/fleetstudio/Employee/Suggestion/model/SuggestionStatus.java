package com.fleetstudio.Employee.Suggestion.model;

public enum SuggestionStatus {
    OPEN("Open"),
    UNDER_REVIEW("Under Review"),
    IMPLEMENTED("Implemented"),
    REJECTED("Rejected"),
    IN_PROGRESS("In Progress"),
    ON_HOLD("On Hold");
    
    private final String displayName;
    
    SuggestionStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}