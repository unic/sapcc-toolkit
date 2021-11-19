package com.unic.sapcc.toolkit.enums;

public enum DeployStrategy {
	ROLLING_UPDATE("ROLLING_UPDATE"),
	RECREATE("RECREATE");

	private final String deployStrategy;

	private DeployStrategy(String value) {
		deployStrategy = value;
	}

	public String getDeployStrategy() {
		return deployStrategy;
	}
}
