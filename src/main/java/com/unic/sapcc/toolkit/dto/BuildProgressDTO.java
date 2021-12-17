package com.unic.sapcc.toolkit.dto;

import com.unic.sapcc.toolkit.enums.BuildStatus;

import java.util.List;

public record BuildProgressDTO(
		String subscriptionCode,
		String buildCode,
		String errorMessage,
		int numberOfTasks,
		int percentage,
		BuildStatus buildStatus,
		List<BuildProgressStartedTaskDTO> startedTasks) {
}
