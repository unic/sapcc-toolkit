package com.unic.sapcc.toolkit.dto;

import java.util.List;

public class BuildProgressDTO implements BodyDTO {

	private String subscriptionCode;
	private String buildCode;
	private String errorMessage;
	private int numberOfTasks;
	private int percentage;
	private String buildStatus;
	private List<BuildProgressStartedTaskDTO> startedTasks;

	public BuildProgressDTO(String subscriptionCode, String buildCode, String errorMessage, int numberOfTasks, int percentage,
			String buildStatus, List<BuildProgressStartedTaskDTO> startedTasks) {
		this.subscriptionCode = subscriptionCode;
		this.buildCode = buildCode;
		this.errorMessage = errorMessage;
		this.numberOfTasks = numberOfTasks;
		this.percentage = percentage;
		this.buildStatus = buildStatus;
		this.startedTasks = startedTasks;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public int getNumberOfTasks() {
		return numberOfTasks;
	}

	public void setNumberOfTasks(int numberOfTasks) {
		this.numberOfTasks = numberOfTasks;
	}

	public int getPercentage() {
		return percentage;
	}

	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	public List<BuildProgressStartedTaskDTO> getStartedTasks() {
		return startedTasks;
	}

	public String getSubscriptionCode() {
		return subscriptionCode;
	}

	public void setSubscriptionCode(String subscriptionCode) {
		this.subscriptionCode = subscriptionCode;
	}

	public String getBuildCode() {
		return buildCode;
	}

	public void setBuildCode(String buildCode) {
		this.buildCode = buildCode;
	}

	public String getBuildStatus() {
		return buildStatus;
	}

	public void setBuildStatus(String buildStatus) {
		this.buildStatus = buildStatus;
	}

	@Override
	public String toString() {
		return "BuildProgressDTO{" +
				"subscriptionCode='" + subscriptionCode + '\'' +
				", buildCode='" + buildCode + '\'' +
				", errorMessage='" + errorMessage + '\'' +
				", numberOfTasks=" + numberOfTasks +
				", percentage=" + percentage +
				", buildStatus='" + buildStatus + '\'' +
				", startedTasks=" + startedTasks +
				'}';
	}
}
