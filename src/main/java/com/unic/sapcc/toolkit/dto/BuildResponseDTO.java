package com.unic.sapcc.toolkit.dto;

public class BuildResponseDTO implements BodyDTO {

	private String subscriptionCode;
	private String code;

	public BuildResponseDTO(String subscriptionCode, String code) {
		this.subscriptionCode = subscriptionCode;
		this.code = code;
	}

	public String getSubscriptionCode() {
		return subscriptionCode;
	}

	public void setSubscriptionCode(String subscriptionCode) {
		this.subscriptionCode = subscriptionCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "CreatedBuildDTO{" +
				"subscriptionCode='" + subscriptionCode + '\'' +
				", code='" + code + '\'' +
				'}';
	}
}
