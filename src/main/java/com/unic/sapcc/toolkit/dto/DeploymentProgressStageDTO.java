package com.unic.sapcc.toolkit.dto;

import java.util.List;

public class DeploymentProgressStageDTO {

	private String name;
	private String type;
	private String startTimestamp;
	private String endTimestamp;
	private String status;
	private String logLink;
	private List<DeploymentProgressStepDTO> deploymentProgressStepDTOList;

	public DeploymentProgressStageDTO(String name, String type, String startTimestamp, String endTimestamp, String status,
			String logLink, List<DeploymentProgressStepDTO> deploymentProgressStepDTOList) {
		this.name = name;
		this.type = type;
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
		this.status = status;
		this.logLink = logLink;
		this.deploymentProgressStepDTOList = deploymentProgressStepDTOList;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLogLink() {
		return logLink;
	}

	public void setLogLink(String logLink) {
		this.logLink = logLink;
	}

	public List<DeploymentProgressStepDTO> getDeploymentProgressStepDTOList() {
		return deploymentProgressStepDTOList;
	}

	public void setDeploymentProgressStepDTOList(List<DeploymentProgressStepDTO> deploymentProgressStepDTOList) {
		this.deploymentProgressStepDTOList = deploymentProgressStepDTOList;
	}

	@Override
	public String toString() {
		return "DeploymentProgressStageDTO{" +
				"name='" + name + '\'' +
				", type='" + type + '\'' +
				", startTimestamp='" + startTimestamp + '\'' +
				", endTimestamp='" + endTimestamp + '\'' +
				", status='" + status + '\'' +
				", logLink='" + logLink + '\'' +
				", deploymentProgressStepDTOList=" + deploymentProgressStepDTOList +
				'}';
	}
}
