package com.project.compliancereport.client;

import com.project.compliancereport.dto.NotificationRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "Notification")
public interface NotificationClient {

    @PostMapping("/api/notifications")
    void send(@RequestBody NotificationRequestDTO request);
}