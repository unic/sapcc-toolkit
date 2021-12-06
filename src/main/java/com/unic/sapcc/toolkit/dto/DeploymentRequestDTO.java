package com.unic.sapcc.toolkit.dto;

import com.unic.sapcc.toolkit.enums.CloudEnvironment;
import com.unic.sapcc.toolkit.enums.DatabaseUpdateMode;
import com.unic.sapcc.toolkit.enums.DeployStrategy;

public record DeploymentRequestDTO(
		String buildCode,
		DatabaseUpdateMode databaseUpdateMode,
		CloudEnvironment environmentCode,
		DeployStrategy strategy) {
}
