package com.unic.sapcc.toolkit.enums;

public enum DeploymentStatus {

	DEPLOYING("DEPLOYING"),
	DEPLOYED("DEPLOYED");

	private final String deploymentStatus;

	private DeploymentStatus(String value) {
		deploymentStatus = value;
	}

	public String getDeploymentStatus() {
		return deploymentStatus;
	}
}
