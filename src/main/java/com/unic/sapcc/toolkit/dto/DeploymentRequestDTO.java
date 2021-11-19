package com.unic.sapcc.toolkit.dto;

import com.unic.sapcc.toolkit.enums.CloudEnvironment;
import com.unic.sapcc.toolkit.enums.DatabaseUpdateMode;
import com.unic.sapcc.toolkit.enums.DeployStrategy;
import org.springframework.util.StringUtils;

public class DeploymentRequestDTO implements BodyDTO {

	private String buildCode;
	private DatabaseUpdateMode databaseUpdateMode;
	private CloudEnvironment environmentCode;
	private DeployStrategy strategy;

	public DeploymentRequestDTO(String buildCode,
			DatabaseUpdateMode databaseUpdateMode, CloudEnvironment environmentCode,
			DeployStrategy strategy) {
		this.buildCode = buildCode;
		this.databaseUpdateMode = databaseUpdateMode;
		this.environmentCode = environmentCode;
		this.strategy = strategy;
	}

	public String getBuildCode() {
		return buildCode;
	}

	public void setBuildCode(String buildCode) {
		this.buildCode = buildCode;
	}

	public DatabaseUpdateMode getDatabaseUpdateMode() {
		return databaseUpdateMode;
	}

	public void setDatabaseUpdateMode(DatabaseUpdateMode databaseUpdateMode) {
		this.databaseUpdateMode = databaseUpdateMode;
	}

	public CloudEnvironment getEnvironmentCode() {
		return environmentCode;
	}

	public void setEnvironmentCode(CloudEnvironment environmentCode) {
		this.environmentCode = environmentCode;
	}

	public DeployStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(DeployStrategy strategy) {
		this.strategy = strategy;
	}

	@Override
	public String toString() {
		return "DeploymentRequestDTO{" +
				"buildCode='" + buildCode + '\'' +
				", databaseUpdateMode=" + databaseUpdateMode +
				", environmentCode='" + environmentCode + '\'' +
				", strategy=" + strategy +
				'}';
	}
}
