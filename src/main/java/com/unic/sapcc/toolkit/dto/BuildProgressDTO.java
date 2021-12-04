package com.unic.sapcc.toolkit.dto;

import java.util.List;

public record BuildProgressDTO(
		String subscriptionCode,
		String buildCode,
		String errorMessage,
		int numberOfTasks,
		int percentage,
		String buildStatus,
		List<BuildProgressStartedTaskDTO> startedTasks) {
}