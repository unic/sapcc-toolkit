package com.unic.sapcc.toolkit.dto;

public class BuildProgressStartedTaskDTO {

	private String name;
	private String startTimestamp;
	private String task;

	public BuildProgressStartedTaskDTO(String name, String startTimestamp, String task) {
		this.name = name;
		this.startTimestamp = startTimestamp;
		this.task = task;
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

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	@Override
	public String toString() {
		return "StartedTaskDTO{" +
				"name='" + name + '\'' +
				", startTimestamp=" + startTimestamp +
				", task='" + task + '\'' +
				'}';
	}
}
