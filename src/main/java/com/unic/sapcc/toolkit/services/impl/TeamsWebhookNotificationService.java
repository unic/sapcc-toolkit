package com.unic.sapcc.toolkit.services.impl;

import com.unic.sapcc.toolkit.dto.BuildProgressDTO;
import com.unic.sapcc.toolkit.dto.BuildRequestDTO;
import com.unic.sapcc.toolkit.dto.DeploymentProgressDTO;
import com.unic.sapcc.toolkit.dto.DeploymentRequestDTO;
import com.unic.sapcc.toolkit.dto.TeamsNotificationRequestDTO;
import com.unic.sapcc.toolkit.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

public class TeamsWebhookNotificationService extends AbstractCloudService implements NotificationService {

	private static final Logger LOG = LoggerFactory.getLogger(TeamsWebhookNotificationService.class);

	@Autowired
	private RestTemplate restTemplate;

	@Value("${notification.teams.webhook.url:#{null}}")
	private Optional<String> teamsWebhookUrl;

	@Override
	public void sendMessage(final String message) {
		if (teamsWebhookUrl.isEmpty()) {
			LOG.debug("No Teams webhook URL defined. Notification skipped.");
			return;
		}

		restTemplate.postForLocation(teamsWebhookUrl.get(), new TeamsNotificationRequestDTO(message));
	}

	@Override
	public <T> String formatMessageForDTO(T dto) {
		if (dto instanceof DeploymentRequestDTO) {
			return "Deployment Request<br>" +
					"BuildCode: " + ((DeploymentRequestDTO) dto).buildCode() + "<br>" +
					"EnvironmentCode: " + ((DeploymentRequestDTO) dto).environmentCode() + "<br>" +
					"DatabaseUpdateMode: " + ((DeploymentRequestDTO) dto).databaseUpdateMode() + "<br>" +
					"Strategy: " + ((DeploymentRequestDTO) dto).strategy();
		}
		if (dto instanceof BuildRequestDTO) {
			return "Build Request<br>" +
					"Application: " + ((BuildRequestDTO) dto).applicationCode() + "<br>" +
					"Branch: " + ((BuildRequestDTO) dto).branch() + "<br>" +
					"Build Name: " + ((BuildRequestDTO) dto).name();
		}
		if (dto instanceof BuildProgressDTO) {
			return "Build Progress<br>" +
					"Build Code: " + ((BuildProgressDTO) dto).buildCode() + "<br>" +
					"Progress: " + ((BuildProgressDTO) dto).percentage() + "<br>" +
					"Build Status: " + ((BuildProgressDTO) dto).buildStatus();
		}
		if (dto instanceof DeploymentProgressDTO) {
			return "Deployment Progress<br>" +
					"Deployment Code: " + ((DeploymentProgressDTO) dto).deploymentCode() + "<br>" +
					"Progress: " + ((DeploymentProgressDTO) dto).percentage() + "<br>" +
					"Deployment Status: " + ((DeploymentProgressDTO) dto).deploymentStatus();
		}

		return dto.toString();
	}
}
