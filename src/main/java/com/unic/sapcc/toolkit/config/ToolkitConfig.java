package com.unic.sapcc.toolkit.config;

import com.unic.sapcc.toolkit.conditions.TeamsWebhookNotificationCondition;
import com.unic.sapcc.toolkit.services.NotificationService;
import com.unic.sapcc.toolkit.services.impl.TeamsWebhookNotificationService;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class ToolkitConfig {
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.setConnectTimeout(Duration.ofMillis(10000)).setReadTimeout(Duration.ofMillis(10000)).build();
	}

	@Bean
	@Conditional(TeamsWebhookNotificationCondition.class)
	public NotificationService getTeamsNotificationService() {
		return new TeamsWebhookNotificationService();
	}
}
