package com.unic.sapcc.toolkit.services.impl;

import com.unic.sapcc.toolkit.dto.BodyDTO;
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

	private final static Logger LOG = LoggerFactory.getLogger(DefaultCloudBuildService.class);

	@Autowired
	public RestTemplate restTemplate;

	@Autowired
	public Environment env;

	@Value("${toolkit.subscriptionCode}")
	private String subscriptionCode;

	@Override
	public String createBuild(String applicationCode, String branch, String name) {
		BuildRequestDTO buildRequestDTO = new BuildRequestDTO(applicationCode, branch, name);
		return createBuild(buildRequestDTO);
	}

	@Override
	public String createBuild(BuildRequestDTO buildRequestDTO) {
		LOG.info("Create Build with params: " + buildRequestDTO);
		HttpEntity<BodyDTO> entity = prepareHttpEntity(buildRequestDTO);
		try {
			ResponseEntity<BuildResponseDTO> createdBuildEntity = restTemplate.exchange(
					"https://portalrotapi.hana.ondemand.com/v2/subscriptions/" + subscriptionCode + "/builds", HttpMethod.POST, entity,
					BuildResponseDTO.class);

			if (createdBuildEntity.getBody() != null) {
				BuildResponseDTO buildResponseDTO = createdBuildEntity.getBody();
				LOG.info("Build created with buildCode: " + buildResponseDTO.getCode());
				return buildResponseDTO.getCode();
			}
		} catch (HttpClientErrorException hce) {
			LOG.error("Error during build creation", hce);
		}
		return null;
	}

	@Override
	public BuildStatus verifyBuildProgress(BuildProgressDTO buildProgressDTO) {
		LOG.debug("Verify build progress: " + buildProgressDTO);
		if (BuildStatus.ERROR.name().equals(buildProgressDTO.getBuildStatus())) {
			return BuildStatus.ERROR;
		}
		if (BuildStatus.BUILDING.name().equals(buildProgressDTO.getBuildStatus())) {
			return BuildStatus.BUILDING;
		}
		if (BuildStatus.SUCCESS.name().equals(buildProgressDTO.getBuildStatus())) {
			return BuildStatus.SUCCESS;
		}
		return BuildStatus.UNKNOWN;
	}

	@Override
	public void handleBuildProgress(String buildCode) throws InterruptedException {
		long startTime = System.currentTimeMillis();

		long sleepTime = Long.parseLong(env.getProperty("toolkit.build.sleepTime", "5"));
		long maxWaitTime = Long.parseLong(env.getProperty("toolkit.build.maxWaitTime", "30"));
		LOG.info(String.format("Build will be watched with polling rate of %d sec and max wait time of %d min", sleepTime, maxWaitTime));

		while (true) {
			if (startTime + maxWaitTime * 1000 * 60 < System.currentTimeMillis()) {
				throw new IllegalStateException("Maximium waiting time of " + maxWaitTime + " minutes reached. Aborting build progress watching process.");
			}
			BuildProgressDTO buildProgress = getBuildProgress(buildCode);
			LOG.info("Build progress (%): " + buildProgress.getPercentage());
			if (BuildStatus.SUCCESS == verifyBuildProgress(buildProgress)) {
				LOG.info("Build status: " + BuildStatus.SUCCESS);
				return ;
			}
			TimeUnit.SECONDS.sleep(sleepTime);
		}
	}

	private BuildProgressDTO getBuildProgress(String buildCode) {
		LOG.info("Retrieving build progress for buildCode: " + buildCode);
		HttpEntity<BodyDTO> entity = prepareHttpEntity(null);
		ResponseEntity<BuildProgressDTO> buildProgressEntity = null;
		try {
			buildProgressEntity = restTemplate.exchange(
					"https://portalrotapi.hana.ondemand.com/v2/subscriptions/" + subscriptionCode + "/builds/" + buildCode
							+ "/progress", HttpMethod.GET, entity, BuildProgressDTO.class);
		} catch (ResourceAccessException raex) {
			LOG.error(raex.getMessage());
		}

		return buildProgressEntity.getBody();
	}
}
