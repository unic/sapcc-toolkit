package com.unic.sapcc.toolkit.enums;

public enum CloudEnvironment {
	d1("d1"),
	s1("s1");

	private String environment;

	private CloudEnvironment(String value) {
		environment = value;
	}

	public String getEnvironment() {
		return environment;
	}
}
