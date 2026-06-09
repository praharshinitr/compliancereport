package com.project.compliancereport.client;

import com.project.compliancereport.dto.KPIReportDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "kpireport")
public interface KPIServiceClient {

    @GetMapping("/api/kpi-reports/{id}")
    KPIReportDTO getKpiReportById(@PathVariable("id") Long id);
}

