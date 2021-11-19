package com.unic.sapcc.toolkit.enums;

public enum BuildStatus {
	UNKNOWN("UNKNOWN"),
	SCHEDULED("SCHEDULED"),
	BUILDING("BUILDING"),
	SUCCESS("SUCCESS"),
	ERROR("ERROR");

	private final String buildStatus;

	private BuildStatus(String value) {
		buildStatus = value;
	}

	public String getBuildStatus() {
		return buildStatus;
	}
}
