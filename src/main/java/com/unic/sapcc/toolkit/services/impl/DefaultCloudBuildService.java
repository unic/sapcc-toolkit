package com.unic.sapcc.toolkit.services.impl;

import com.unic.sapcc.toolkit.dto.BuildProgressDTO;
import com.unic.sapcc.toolkit.dto.BuildRequestDTO;
import com.unic.sapcc.toolkit.dto.BuildResponseDTO;
import com.unic.sapcc.toolkit.enums.BuildStatus;
import com.unic.sapcc.toolkit.services.CloudBuildService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Service
public class DefaultCloudBuildService extends AbstractCloudService implements CloudBuildService {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultCloudBuildService.class);

	public final RestTemplate restTemplate;

	public final Environment env;

	@Value("${toolkit.subscriptionCode}")
	private String subscriptionCode;

	public DefaultCloudBuildService(RestTemplate restTemplate, Environment env) {
		this.restTemplate = restTemplate;
		this.env = env;
	}

	@Override
	public String createBuild(String applicationCode, String branch, String name) {
		BuildRequestDTO buildRequestDTO = new BuildRequestDTO(applicationCode, branch, name);
		return createBuild(buildRequestDTO);
	}

	@Override
	public String createBuild(BuildRequestDTO buildRequestDTO) {
		LOG.info("Create Build with params: {}", buildRequestDTO);
		HttpEntity<?> entity = prepareHttpEntity(buildRequestDTO);
		try {
			ResponseEntity<BuildResponseDTO> createdBuildEntity = restTemplate.exchange(
					PORTAL_API + subscriptionCode + "/builds", HttpMethod.POST, entity,
					BuildResponseDTO.class);

			if (createdBuildEntity.getBody() != null) {
				BuildResponseDTO buildResponseDTO = createdBuildEntity.getBody();
				if (buildResponseDTO == null) {
					throw new IllegalStateException();
				}
				LOG.info("Build created with buildCode '{}'", buildResponseDTO.code());
				return buildResponseDTO.code();
			}
		} catch (HttpClientErrorException hce) {
			LOG.error("Error during build creation", hce);
		}
		return null;
	}

	@Override
	public BuildStatus verifyBuildProgress(BuildProgressDTO buildProgressDTO) {
		LOG.debug("Verify build progress: {}", buildProgressDTO);
		if (BuildStatus.ERROR.name().equals(buildProgressDTO.buildStatus())) {
			return BuildStatus.ERROR;
		}
		if (BuildStatus.BUILDING.name().equals(buildProgressDTO.buildStatus())) {
			return BuildStatus.BUILDING;
		}
		if (BuildStatus.SUCCESS.name().equals(buildProgressDTO.buildStatus())) {
			return BuildStatus.SUCCESS;
		}
		return BuildStatus.UNKNOWN;
	}

	@Override
	public void handleBuildProgress(String buildCode) throws InterruptedException {
		long startTime = System.currentTimeMillis();

		long sleepTime = Long.parseLong(env.getProperty("toolkit.build.sleepTime", "5"));
		long maxWaitTime = Long.parseLong(env.getProperty("toolkit.build.maxWaitTime", "30"));
		LOG.info("Build will be watched with polling rate of {} sec and max wait time of {} min", sleepTime, maxWaitTime);

		while (true) {
			if (startTime + maxWaitTime * 1000 * 60 < System.currentTimeMillis()) {
				throw new IllegalStateException(
						"Maximum waiting time of " + maxWaitTime + " minutes reached. Aborting build progress watching process.");
			}
			BuildProgressDTO buildProgress = getBuildProgress(buildCode);
			LOG.info("Build progress: {} %", buildProgress.percentage());
			if (BuildStatus.SUCCESS == verifyBuildProgress(buildProgress)) {
				LOG.info("Build status: {}", BuildStatus.SUCCESS);
				return;
			}
			TimeUnit.SECONDS.sleep(sleepTime);
		}
	}

	private BuildProgressDTO getBuildProgress(String buildCode) {
		LOG.info("Retrieving build progress for buildCode '{}'" , buildCode);
		HttpEntity<?> entity = prepareHttpEntity(null);
		try {
			ResponseEntity<BuildProgressDTO> buildProgressEntity = restTemplate.exchange(
					PORTAL_API + subscriptionCode + "/builds/" + buildCode + "/progress",
					HttpMethod.GET,
					entity,
					BuildProgressDTO.class);
			if (buildProgressEntity.getBody() == null) {
				throw new IllegalStateException();
			}
			return buildProgressEntity.getBody();
		} catch (ResourceAccessException raex) {
			LOG.error(raex.getMessage(), raex);
			throw new IllegalStateException(raex);
		}
	}
}
