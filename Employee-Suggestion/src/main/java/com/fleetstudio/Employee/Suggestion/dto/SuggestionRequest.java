package com.fleetstudio.Employee.Suggestion.dto;

import lombok.Data;

@Data
public class SuggestionRequest {
    private String title;
    private String description;
    private boolean anonymous;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }
}
