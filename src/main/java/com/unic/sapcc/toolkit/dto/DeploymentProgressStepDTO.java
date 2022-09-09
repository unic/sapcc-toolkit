package com.unic.sapcc.toolkit.dto;

import java.util.List;

public record DeploymentProgressStepDTO(
		String code,
		String name,
		String startTimestamp,
		String endTimestamp,
		String message,
		String status,
		List<DeploymentProgressStepDTO> children) {
}
