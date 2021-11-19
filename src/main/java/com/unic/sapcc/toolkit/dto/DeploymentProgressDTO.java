package com.unic.sapcc.toolkit.dto;

import com.unic.sapcc.toolkit.enums.DeploymentStatus;
import java.util.List;

public class DeploymentProgressDTO {

	private String subscriptionCode;
	private String deploymentCode;
	private DeploymentStatus deploymentStatus;
	private int percentage;
	List<DeploymentProgressStageDTO> deploymentProgressStageDTOList;

	public DeploymentProgressDTO(String subscriptionCode, String deploymentCode, DeploymentStatus deploymentStatus, int percentage,
			List<DeploymentProgressStageDTO> deploymentProgressStageDTOList) {
		this.subscriptionCode = subscriptionCode;
		this.deploymentCode = deploymentCode;
		this.deploymentStatus = deploymentStatus;
		this.percentage = percentage;
		this.deploymentProgressStageDTOList = deploymentProgressStageDTOList;
	}

	public String getSubscriptionCode() {
		return subscriptionCode;
	}

	public void setSubscriptionCode(String subscriptionCode) {
		this.subscriptionCode = subscriptionCode;
	}

	public String getDeploymentCode() {
		return deploymentCode;
	}

	public void setDeploymentCode(String deploymentCode) {
		this.deploymentCode = deploymentCode;
	}

	public DeploymentStatus getDeploymentStatus() {
		return deploymentStatus;
	}

	public void setDeploymentStatus(DeploymentStatus deploymentStatus) {
		this.deploymentStatus = deploymentStatus;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public List<DeploymentProgressStageDTO> getDeploymentProgressStageDTOList() {
		return deploymentProgressStageDTOList;
	}

	public void setDeploymentProgressStageDTOList(
			List<DeploymentProgressStageDTO> deploymentProgressStageDTOList) {
		this.deploymentProgressStageDTOList = deploymentProgressStageDTOList;
	}

	@Override
	public String toString() {
		return "DeploymentProgressDTO{" +
				"subscriptionCode='" + subscriptionCode + '\'' +
				", deploymentCode='" + deploymentCode + '\'' +
				", deploymentStatus=" + deploymentStatus +
				", percentage=" + percentage +
				", deploymentProgressStageDTOList=" + deploymentProgressStageDTOList +
				'}';
	}
}
