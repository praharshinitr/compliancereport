package com.project.compliancereport.dto;

import jakarta.validation.constraints.NotBlank;

public class ComplianceReportRequestDTO {

    @NotBlank(message = "Scope is required")
    private String scope;

    // metrics is optional — system generates it from KPI values
    private String metrics;

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public String getMetrics() { return metrics; }
    public void setMetrics(String metrics) { this.metrics = metrics; }
}