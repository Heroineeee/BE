package com.kkinikong.be.report.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ReportStoreRequest(@Size(max = 500) String description) {}
