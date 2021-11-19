package com.unic.sapcc.toolkit.enums;

public enum DatabaseUpdateMode {

	NONE("NONE"),
	UPDATE("UPDATE"),
	INITIALIZE("INITIALIZE");

	private final String databaseUpdateMode;

	private DatabaseUpdateMode(String value) {
		databaseUpdateMode = value;
	}

	public String getDatabaseUpdateMode() {
		return databaseUpdateMode;
	}
}
