package com.unic.sapcc.toolkit.dto;

import java.util.List;

public class DeploymentProgressStepDTO {
	private String code;
	private String name;
	private String startTimestamp;
	private String endTimestamp;
	private String message;
	private String status;
	private List<DeploymentProgressStepDTO> children;

	public DeploymentProgressStepDTO(String code, String name, String startTimestamp, String endTimestamp, String message,
			String status, List<DeploymentProgressStepDTO> children) {
		this.code = code;
		this.name = name;
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
		this.message = message;
		this.status = status;
		this.children = children;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(String startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public String getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(String endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<DeploymentProgressStepDTO> getChildren() {
		return children;
	}

	public void setChildren(List<DeploymentProgressStepDTO> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return "DeploymentProgressStepDTO{" +
				"code='" + code + '\'' +
				", name='" + name + '\'' +
				", startTimestamp='" + startTimestamp + '\'' +
				", endTimestamp='" + endTimestamp + '\'' +
				", message='" + message + '\'' +
				", status='" + status + '\'' +
				", children=" + children +
				'}';
	}
}
