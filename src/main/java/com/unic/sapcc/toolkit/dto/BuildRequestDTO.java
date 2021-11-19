package com.unic.sapcc.toolkit.dto;

public class BuildRequestDTO implements BodyDTO {

	private String applicationCode;
	private String branch;
	private String name;

	public BuildRequestDTO(String applicationCode, String branch, String name) {
		this.applicationCode = applicationCode;
		this.branch = branch;
		this.name = name;
	}

	public String getApplicationCode() {
		return applicationCode;
	}

	public void setApplicationCode(String applicationCode) {
		this.applicationCode = applicationCode;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "PostBuildDTO{" +
				"applicationCode='" + applicationCode + '\'' +
				", branch='" + branch + '\'' +
				", name='" + name + '\'' +
				'}';
	}
}
