package com.project.compliancereport.db;

import com.project.compliancereport.model.ComplianceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ComplianceReportRepository extends JpaRepository<ComplianceReport, Long> {
    List<ComplianceReport> findByScope(String scope);
    List<ComplianceReport> findByStatus(String status);
    List<ComplianceReport> findByGeneratedDateBetween(LocalDate start, LocalDate end);
    ComplianceReport findFirstByScopeOrderByGeneratedDateDesc(String scope);
}