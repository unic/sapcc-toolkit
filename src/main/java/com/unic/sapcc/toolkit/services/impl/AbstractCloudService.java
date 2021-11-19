package com.unic.sapcc.toolkit.services.impl;

import com.unic.sapcc.toolkit.dto.BodyDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

@Service
public abstract class AbstractCloudService {

	@Value("${toolkit.apiKey}")
	private String apiKey;

	public HttpEntity<BodyDTO> prepareHttpEntity(BodyDTO bodyDTO) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(apiKey);
		HttpEntity<BodyDTO> entity = new HttpEntity<BodyDTO>(bodyDTO, headers);
		return entity;
	}
}
