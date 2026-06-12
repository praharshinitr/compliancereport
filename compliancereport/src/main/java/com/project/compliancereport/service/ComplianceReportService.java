package com.project.compliancereport.service;

import com.project.compliancereport.client.AuditLogClient;
import com.project.compliancereport.client.KPIServiceClient;
import com.project.compliancereport.client.NotificationClient;
import com.project.compliancereport.db.ComplianceReportRepository;
import com.project.compliancereport.dto.*;
import com.project.compliancereport.dto.NotificationRequestDTO;
import com.project.compliancereport.exception.ResourceNotFoundException;
import com.project.compliancereport.model.ComplianceReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplianceReportService {

    @Autowired
    private ComplianceReportRepository complianceReportRepository;

    @Autowired
    private KPIServiceClient kpiServiceClient;

    @Autowired
    private AuditLogClient auditLogClient;

    @Autowired
    private NotificationClient notificationClient;

    private static final double SHRINKAGE_THRESHOLD = 5.0;
    private static final double MIN_STOCK_TURNOVER  = 2.0;
    private static final double MIN_SALES_GROWTH    = 0.0;

    // ── AUDIT HELPER ──────────────────────────────────────────────────────────
    private void sendAudit(String action, String description) {
        try {
            auditLogClient.log(new AuditLogRequestDTO(
                    1L,
                    action,
                    "compliance-service"
            ));
        } catch (Exception e) {
            System.err.println("[AuditLog] Failed to log: " + e.getMessage());
        }
    }

    // ── NOTIFICATION HELPER ───────────────────────────────────────────────────
    private void sendNotification(String scope, String remarks) {
        try {
            notificationClient.send(new NotificationRequestDTO(
                    1L,
                    "FAIL verdict for scope: " + scope + " | " + remarks,
                    "COMPLIANCE"
            ));
        } catch (Exception e) {
            System.err.println("[Notification] Failed to send: " + e.getMessage());
        }
    }

    // ── GENERATE FROM KPI ─────────────────────────────────────────────────────
    public ComplianceReportResponseDTO generateFromKpi(Long kpiReportId) {
        KPIReportDTO kpiReport = kpiServiceClient.getKpiReportById(kpiReportId);
        ComplianceReportRequestDTO dto = new ComplianceReportRequestDTO();
        dto.setScope(kpiReport.getScope());
        dto.setMetrics(kpiReport.getMetrics());
        return insertComplianceReport(dto);
    }

    // ── INSERT ────────────────────────────────────────────────────────────────
    public ComplianceReportResponseDTO insertComplianceReport(ComplianceReportRequestDTO dto) {
        try {
            double stockTurnover = 0.0, salesGrowth = 0.0, shrinkageRate = 0.0;

            try {
                String metrics = dto.getMetrics();
                if (metrics != null && !metrics.isEmpty()) {
                    for (String part : metrics.split("\\|")) {
                        part = part.trim();
                        if (part.startsWith("Stock Turnover:"))
                            stockTurnover = Double.parseDouble(part.replace("Stock Turnover:", "").trim());
                        else if (part.startsWith("Sales Growth:"))
                            salesGrowth = Double.parseDouble(part.replace("Sales Growth:", "").replace("%", "").trim());
                        else if (part.startsWith("Shrinkage:"))
                            shrinkageRate = Double.parseDouble(part.replace("Shrinkage:", "").replace("%", "").trim());
                    }
                }
            } catch (Exception ignored) {}

            int failures = 0;
            if (shrinkageRate > SHRINKAGE_THRESHOLD) failures++;
            if (salesGrowth   < MIN_SALES_GROWTH)    failures++;
            if (stockTurnover < MIN_STOCK_TURNOVER)  failures++;

            String status = failures == 0 ? "PASS" : failures >= 2 ? "FAIL" : "WARNING";

            StringBuilder remarks = new StringBuilder();
            if (failures == 0) {
                remarks.append("All KPI thresholds met.");
            } else {
                if (shrinkageRate > SHRINKAGE_THRESHOLD)
                    remarks.append(String.format("Shrinkage %.2f%% exceeds limit of %.1f%%. ", shrinkageRate, SHRINKAGE_THRESHOLD));
                if (salesGrowth < MIN_SALES_GROWTH)
                    remarks.append(String.format("Sales growth is negative (%.2f%%). ", salesGrowth));
                if (stockTurnover < MIN_STOCK_TURNOVER)
                    remarks.append(String.format("Stock turnover %.2f is below minimum of %.1f.", stockTurnover, MIN_STOCK_TURNOVER));
            }

            ComplianceReport report = new ComplianceReport();
            report.setScope(dto.getScope());
            report.setMetrics(dto.getMetrics());
            report.setGeneratedDate(LocalDate.now());
            report.setStatus(status);
            report.setRemarks(remarks.toString().trim());
            report.setStockTurnover(stockTurnover);
            report.setSalesGrowth(salesGrowth);
            report.setShrinkageRate(shrinkageRate);

            ComplianceReportResponseDTO result = mapToDTO(complianceReportRepository.save(report));

            sendAudit("INSERT", "Report created | ID: " + result.getReportId()
                    + " | Scope: " + dto.getScope() + " | Status: " + status);

            if ("FAIL".equals(status)) {
                sendNotification(dto.getScope(), remarks.toString().trim());
            }

            return result;

        } catch (Exception ex) {
            sendAudit("INSERT_FAILED", "Scope: " + dto.getScope() + " | Error: " + ex.getMessage());
            throw ex;
        }
    }

    // ── UPDATE ────────────────────────────────────────────────────────────────
    public ComplianceReportResponseDTO updateComplianceReport(Long id, ComplianceReportRequestDTO dto) {
        ComplianceReport report = complianceReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compliance report not found with ID: " + id));

        String before = "Scope: " + report.getScope() + " | Metrics: " + report.getMetrics();

        try {
            report.setScope(dto.getScope());
            report.setMetrics(dto.getMetrics());
            ComplianceReportResponseDTO result = mapToDTO(complianceReportRepository.save(report));

            sendAudit("UPDATE", "Report updated | ID: " + id
                    + " | Before: " + before
                    + " | After: Scope: " + dto.getScope() + " | Metrics: " + dto.getMetrics());

            return result;
        } catch (Exception ex) {
            sendAudit("UPDATE_FAILED", "ID: " + id + " | Error: " + ex.getMessage());
            throw ex;
        }
    }

    // ── DELETE ────────────────────────────────────────────────────────────────
    public void deleteComplianceReport(Long id) {
        ComplianceReport report = complianceReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compliance report not found with ID: " + id));
        try {
            report.setStatus("ARCHIVED");
            complianceReportRepository.save(report);

            sendAudit("ARCHIVE", "Report archived | ID: " + id + " | Scope: " + report.getScope());
        } catch (Exception ex) {
            sendAudit("ARCHIVE_FAILED", "ID: " + id + " | Error: " + ex.getMessage());
            throw ex;
        }
    }

    // ── READ operations ───────────────────────────────────────────────────────
    public ComplianceReportResponseDTO getComplianceReport(Long id) {
        return mapToDTO(complianceReportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Compliance report not found with ID: " + id)));
    }

    public List<ComplianceReportResponseDTO> getAllComplianceReports() {
        return complianceReportRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ComplianceReportResponseDTO> getByScope(String scope) {
        return complianceReportRepository.findByScope(scope).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public ComplianceReportResponseDTO getLatestByScope(String scope) {
        ComplianceReport report = complianceReportRepository.findFirstByScopeOrderByGeneratedDateDesc(scope);
        if (report == null) throw new ResourceNotFoundException("No compliance report found for scope: " + scope);
        return mapToDTO(report);
    }

    public List<ComplianceReportResponseDTO> getByDateRange(LocalDate start, LocalDate end) {
        return complianceReportRepository.findByGeneratedDateBetween(start, end).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Page<ComplianceReportResponseDTO> getAllPagesWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return complianceReportRepository.findAll(pageable).map(this::mapToDTO);
    }

    // ── MAP ENTITY TO DTO ─────────────────────────────────────────────────────
    private ComplianceReportResponseDTO mapToDTO(ComplianceReport r) {
        ComplianceReportResponseDTO dto = new ComplianceReportResponseDTO();
        dto.setReportId(r.getReportId());
        dto.setScope(r.getScope());
        dto.setMetrics(r.getMetrics());
        dto.setGeneratedDate(r.getGeneratedDate());
        dto.setStatus(r.getStatus());
        dto.setRemarks(r.getRemarks());
        dto.setStockTurnover(r.getStockTurnover());
        dto.setSalesGrowth(r.getSalesGrowth());
        dto.setShrinkageRate(r.getShrinkageRate());
        return dto;
    }
}