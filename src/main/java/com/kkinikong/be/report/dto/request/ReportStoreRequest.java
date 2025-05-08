package com.kkinikong.be.report.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ReportStoreRequest(@Size(max = 500) @Nullable String description) {}
