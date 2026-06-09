package com.project.compliancereport.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "compliance_report")
public class ComplianceReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @NotBlank(message = "Scope is required")
    private String scope;

    private String metrics;

    private LocalDate generatedDate;

    // status now holds PASS / WARNING / FAIL
    private String status;

    // NEW — explains which threshold failed
    private String remarks;

    // NEW — individual KPI values parsed from metrics
    private Double stockTurnover;
    private Double salesGrowth;
    private Double shrinkageRate;

    public ComplianceReport() {}

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ComplianceReport that = (ComplianceReport) o;
        return Objects.equals(reportId, that.reportId);
    }

    @Override
    public int hashCode() { return Objects.hash(reportId); }
}