package com.unic.sapcc.toolkit.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public abstract class AbstractCloudService {

	public static final String PORTAL_API = "https://portalrotapi.hana.ondemand.com/v2/subscriptions/";
	@Value("${toolkit.apiKey:#{null}}")
	private String apiKey;

	public <T> HttpEntity<T> prepareHttpEntity(T bodyDTO) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(apiKey);
		return new HttpEntity<>(bodyDTO, headers);
	}
}
