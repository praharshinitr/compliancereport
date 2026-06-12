package com.project.compliancereport.dto;

public class NotificationRequestDTO {

    private Long userId;
    private String message;
    private String category;

    public NotificationRequestDTO() {}

    public NotificationRequestDTO(Long userId, String message, String category) {
        this.userId = userId;
        this.message = message;
        this.category = category;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}