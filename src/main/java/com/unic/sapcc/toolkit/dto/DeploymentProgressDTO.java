package com.unic.sapcc.toolkit.dto;

import com.unic.sapcc.toolkit.enums.DeploymentStatus;

import java.util.List;

public record DeploymentProgressDTO(
		String subscriptionCode,
		String deploymentCode,
		DeploymentStatus deploymentStatus,
		int percentage,
		List<DeploymentProgressStageDTO> deploymentProgressStageDTOList) {
}
