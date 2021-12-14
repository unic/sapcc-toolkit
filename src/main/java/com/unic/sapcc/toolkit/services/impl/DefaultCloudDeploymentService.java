package com.unic.sapcc.toolkit.services.impl;

import com.unic.sapcc.toolkit.dto.DeploymentProgressDTO;
import com.unic.sapcc.toolkit.dto.DeploymentRequestDTO;
import com.unic.sapcc.toolkit.dto.DeploymentResponseDTO;
import com.unic.sapcc.toolkit.enums.CloudEnvironment;
import com.unic.sapcc.toolkit.enums.DatabaseUpdateMode;
import com.unic.sapcc.toolkit.enums.DeployStrategy;
import com.unic.sapcc.toolkit.enums.DeploymentStatus;
import com.unic.sapcc.toolkit.services.CloudDeploymentService;
import com.unic.sapcc.toolkit.services.NotificationService;
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

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
public class DefaultCloudDeploymentService extends AbstractCloudService implements CloudDeploymentService {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultCloudDeploymentService.class);

	public final RestTemplate restTemplate;

	public final Environment env;

	@Autowired(required = false)
	private Optional<NotificationService> notificationService;

	@Value("${toolkit.subscriptionCode:#{null}}")
	private String subscriptionCode;

	public DefaultCloudDeploymentService(RestTemplate restTemplate, Environment env) {
		this.restTemplate = restTemplate;
		this.env = env;
	}

	@Override
	public String createDeployment(DeploymentRequestDTO deploymentRequestDTO) {
		LOG.info("Starting deployment with requestDTO: {}", deploymentRequestDTO);
		HttpEntity<DeploymentRequestDTO> entity = prepareHttpEntity(deploymentRequestDTO);
		try {
			ResponseEntity<DeploymentResponseDTO> createDeploymentEntity = restTemplate.exchange(
					PORTAL_API + subscriptionCode + "/deployments",
					HttpMethod.POST,
					entity,
					DeploymentResponseDTO.class);
			if (createDeploymentEntity.getBody() == null) {
				throw new IllegalStateException();
			}
			return createDeploymentEntity.getBody().code();
		} catch (ResourceAccessException raex) {
			LOG.error("Error while retrieving create-deployment response", raex);
		}
		return null;
	}

	@Override
	public DeploymentRequestDTO createDeploymentRequestDTO(String buildCode, DatabaseUpdateMode deployDatabaseUpdateMode,
			CloudEnvironment deployEnvironmentCode, DeployStrategy deployStrategy) {
		DeploymentRequestDTO deploymentRequestDTO = new DeploymentRequestDTO(buildCode, deployDatabaseUpdateMode, deployEnvironmentCode,
				deployStrategy);
		LOG.info("New Deployment Request DTO created: {}", deploymentRequestDTO);
		return deploymentRequestDTO;
	}

	@Override
	public void handleDeploymentProgress(String deploymentCode) throws InterruptedException, IllegalStateException {
		long startTime = System.currentTimeMillis();

		long sleepTime = Long.parseLong(env.getProperty("toolkit.deploy.sleepTime", "5"));
		long maxWaitTime = Long.parseLong(env.getProperty("toolkit.deploy.maxWaitTime", "30"));
		LOG.info("Deployment will be watched with polling rate of {} sec and max wait time of {} min", sleepTime,
				maxWaitTime);

		while (true) {
			if (startTime + maxWaitTime * 1000 * 60 < System.currentTimeMillis()) {
				throw new IllegalStateException(
						"Maximum waiting time of " + maxWaitTime + " seconds reached. Aborting deployment progress watching process.");
			}
			DeploymentProgressDTO deploymentProgress = getDeploymentProgress(deploymentCode);
			if (deploymentProgress != null) {
				LOG.info("Deployment progress {}%", deploymentProgress.percentage());
				if (DeploymentStatus.DEPLOYED.name().equals(deploymentProgress.deploymentStatus())) {
					LOG.info("Deployment progress: {}", DeploymentStatus.DEPLOYED);
					if (notificationService.isPresent()) {
						notificationService.get().sendMessage(notificationService.get().formatMessageForDTO(deploymentProgress));
					}
					return;
				}
			}

			TimeUnit.SECONDS.sleep(sleepTime);
		}
	}

	private DeploymentProgressDTO getDeploymentProgress(String deploymentCode) {
		LOG.info("Retrieving deployment progress for deploymentCode: {}", deploymentCode);
		HttpEntity<?> entity = prepareHttpEntity(null);
		ResponseEntity<DeploymentProgressDTO> deploymentProgressEntity;
		try {
			deploymentProgressEntity = restTemplate.exchange(
					PORTAL_API + subscriptionCode + "/deployments/" + deploymentCode + "/progress",
					HttpMethod.GET,
					entity,
					DeploymentProgressDTO.class);
		} catch (ResourceAccessException raex) {
			LOG.error(raex.getMessage());
			return null;
		}

		return deploymentProgressEntity.getBody();
	}
}
