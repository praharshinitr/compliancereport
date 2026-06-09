package com.project.compliancereport.dto;

import java.time.LocalDate;

public class ComplianceReportResponseDTO {

    private Long reportId;
    private String scope;
    private String metrics;
    private LocalDate generatedDate;
    private String status;

    // NEW — returned in response
    private String remarks;
    private Double stockTurnover;
    private Double salesGrowth;
    private Double shrinkageRate;

    public Long getReportId() { return reportId; }
    public void setReportId(Long reportId) { this.reportId = reportId; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }

    public String getMetrics() { return metrics; }
    public void setMetrics(String metrics) { this.metrics = metrics; }

    public LocalDate getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDate generatedDate) { this.generatedDate = generatedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public Double getStockTurnover() { return stockTurnover; }
    public void setStockTurnover(Double stockTurnover) { this.stockTurnover = stockTurnover; }

    public Double getSalesGrowth() { return salesGrowth; }
    public void setSalesGrowth(Double salesGrowth) { this.salesGrowth = salesGrowth; }

    public Double getShrinkageRate() { return shrinkageRate; }
    public void setShrinkageRate(Double shrinkageRate) { this.shrinkageRate = shrinkageRate; }
}