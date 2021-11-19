package com.unic.sapcc.toolkit.services.impl;

import com.unic.sapcc.toolkit.dto.BodyDTO;
import com.unic.sapcc.toolkit.dto.DeploymentProgressDTO;
import com.unic.sapcc.toolkit.dto.DeploymentRequestDTO;
import com.unic.sapcc.toolkit.dto.DeploymentResponseDTO;
import com.unic.sapcc.toolkit.enums.CloudEnvironment;
import com.unic.sapcc.toolkit.enums.DatabaseUpdateMode;
import com.unic.sapcc.toolkit.enums.DeployStrategy;
import com.unic.sapcc.toolkit.enums.DeploymentStatus;
import com.unic.sapcc.toolkit.services.CloudDeploymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Service
public class DefaultCloudDeploymentService extends AbstractCloudService implements CloudDeploymentService {

	private final static Logger LOG = LoggerFactory.getLogger(DefaultCloudDeploymentService.class);

	@Autowired
	public RestTemplate restTemplate;

	@Autowired
	public Environment env;

	@Value("${toolkit.subscriptionCode}")
	private String subscriptionCode;

	@Override
	public String createDeployment(DeploymentRequestDTO deploymentRequestDTO) {
		LOG.info("Starting deployment with requestDTO: " + deploymentRequestDTO);
		HttpEntity<BodyDTO> entity = prepareHttpEntity(deploymentRequestDTO);
		ResponseEntity<DeploymentResponseDTO> createDeploymentEntity = restTemplate.exchange(
				"https://portalrotapi.hana.ondemand.com/v2/subscriptions/" + subscriptionCode + "/deployments", HttpMethod.POST, entity,
				DeploymentResponseDTO.class);

		return createDeploymentEntity.getBody().getCode();
	}

	@Override
	public DeploymentRequestDTO createDeploymentRequestDTO(String buildCode, DatabaseUpdateMode deployDatabaseUpdateMode,
			CloudEnvironment deployEnvironmentCode, DeployStrategy deployStrategy) {
		DeploymentRequestDTO deploymentRequestDTO = new DeploymentRequestDTO(buildCode, deployDatabaseUpdateMode, deployEnvironmentCode,
				deployStrategy);
		LOG.info("New Deployment Request DTO created: " + deploymentRequestDTO);
		return deploymentRequestDTO;
	}

	@Override
	public boolean handleDeploymentProgress(String deploymentCode) throws InterruptedException {
		long startTime = System.currentTimeMillis();

		long sleepTime = Long.parseLong(env.getProperty("toolkit.deploy.sleepTime", "5"));
		long maxWaitTime = Long.parseLong(env.getProperty("toolkit.deploy.maxWaitTime", "30"));
		LOG.info(String.format("Deployment will be watched with polling rate of %d sec and max wait time of %d min", sleepTime, maxWaitTime));

		while (true) {
			if (startTime + maxWaitTime * 1000 * 60 < System.currentTimeMillis()) {
				LOG.info("Maximium waiting time of " + maxWaitTime + " seconds reached. Aborting deployment progress watching process.");
				return false;
			}
			DeploymentProgressDTO deploymentProgress = getDeploymentProgress(deploymentCode);
			if (deploymentProgress != null) {
				LOG.info("Deployment progress (%): " + deploymentProgress.getPercentage());
				if (DeploymentStatus.DEPLOYED.equals(deploymentProgress.getDeploymentStatus())) {
					LOG.info("Deployment progress: " + DeploymentStatus.DEPLOYED);
					return true;
				}
			}

			TimeUnit.SECONDS.sleep(sleepTime);
		}
	}

	private DeploymentProgressDTO getDeploymentProgress(String deploymentCode) {
		LOG.info("Retrieving deployment progress for deploymentCode: " + deploymentCode);
		HttpEntity<BodyDTO> entity = prepareHttpEntity(null);
		ResponseEntity<DeploymentProgressDTO> deploymentProgressEntity;
		try {
			deploymentProgressEntity = restTemplate.exchange(
					"https://portalrotapi.hana.ondemand.com/v2/subscriptions/" + subscriptionCode + "/deployments/" + deploymentCode
							+ "/progress", HttpMethod.GET, entity, DeploymentProgressDTO.class);
		} catch (ResourceAccessException raex) {
			LOG.error(raex.getMessage());
			return null;
		}

		return deploymentProgressEntity.getBody();
	}
}
