package com.unic.sapcc.toolkit.dto;

import java.util.Collection;

public record DeploymentHistoryListResponseDTO(
		Collection<DeploymentHistoryResponseDTO> value) {
}
