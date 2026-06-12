package com.project.compliancereport.client;

import com.project.compliancereport.dto.AuditLogRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "AuditLogApplication")
public interface AuditLogClient {

    @PostMapping("/api/audit-logs")
    void log(@RequestBody AuditLogRequestDTO request);
}