package com.unic.sapcc.toolkit.dto;

import com.unic.sapcc.toolkit.enums.CloudEnvironment;
import com.unic.sapcc.toolkit.enums.DatabaseUpdateMode;
import com.unic.sapcc.toolkit.enums.DeployStrategy;
import com.unic.sapcc.toolkit.enums.DeploymentStatus;

import java.time.ZonedDateTime;

public record DeploymentHistoryResponseDTO(
		String subscriptionCode,
		String code,
		String createdBy,
		ZonedDateTime createdTimestamp,
		String buildCode,
		CloudEnvironment environmentCode,
		DatabaseUpdateMode databaseUpdateMode,
		DeployStrategy strategy,
		ZonedDateTime scheduledTimestamp,
		ZonedDateTime deployedTimestamp,
		ZonedDateTime failedTimestamp,
		ZonedDateTime undeployedTimestamp,
		DeploymentStatus status)
{}