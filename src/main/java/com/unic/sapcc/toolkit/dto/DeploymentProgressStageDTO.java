package com.unic.sapcc.toolkit.dto;

import java.util.List;

public record DeploymentProgressStageDTO(
		String name,
		String type,
		String startTimestamp,
		String endTimestamp,
		String status,
		String logLink,
		List<DeploymentProgressStepDTO> deploymentProgressStepDTOList) {
}
