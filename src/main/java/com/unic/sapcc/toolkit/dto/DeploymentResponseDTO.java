package com.unic.sapcc.toolkit.dto;

public class DeploymentResponseDTO {

	private String subscriptionCode;
	private String code;

	public DeploymentResponseDTO(String subscriptionCode, String code) {
		this.subscriptionCode = subscriptionCode;
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSubscriptionCode() {
		return subscriptionCode;
	}

	public void setSubscriptionCode(String subscriptionCode) {
		this.subscriptionCode = subscriptionCode;
	}
}
