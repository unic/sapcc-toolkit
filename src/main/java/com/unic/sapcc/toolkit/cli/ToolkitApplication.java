package com.unic.sapcc.toolkit.cli;

import com.unic.sapcc.toolkit.dto.DeploymentRequestDTO;
import com.unic.sapcc.toolkit.enums.CloudEnvironment;
import com.unic.sapcc.toolkit.enums.DatabaseUpdateMode;
import com.unic.sapcc.toolkit.enums.DeployStrategy;
import com.unic.sapcc.toolkit.services.impl.DefaultCloudBuildService;
import com.unic.sapcc.toolkit.services.impl.DefaultCloudDeploymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDate;

@SpringBootApplication
@ComponentScan(basePackages = "com.unic.sapcc.toolkit")
public class ToolkitApplication implements CommandLineRunner {

	private static final Logger LOG = LoggerFactory.getLogger(ToolkitApplication.class);

	@Autowired
	public DefaultCloudBuildService cloudBuildService;

	@Autowired
	public DefaultCloudDeploymentService cloudDeploymentService;

	@Autowired
	public Environment env;

	public static void main(String[] args) {
		LOG.info("Toolkit started");
		SpringApplication.run(ToolkitApplication.class, args);
		LOG.info("Toolkit finished");
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder
				.setConnectTimeout(Duration.ofMillis(3000))
				.setReadTimeout(Duration.ofMillis(3000))
				.build();
	}

	@Override
	public void run(String... args) throws Exception {
		LOG.info("Following arguments given");
		for (String arg : args) {
			LOG.info("Argument: " + arg);
		}
		if (hasInvalidPropertiesAndArguments()) {
			LOG.info("Aborting toolkit because of invalid arguments / properties");
			return;
		}

		String buildCode = null;
		String deploymentCode = null;
		boolean buildSuccessful = false;
		boolean deploymentSuccessful = false;

		if (Boolean.parseBoolean(env.getProperty("toolkit.build", "false"))) {
			LOG.info("Build will be started");
			buildCode = createBuild();
		}

		buildCode = env.getProperty("toolkit.deploy.buildCode", buildCode);
		if (StringUtils.hasText(buildCode)) {
			LOG.info("Build progress will be watched: " + buildCode);
			buildSuccessful = watchBuildProgress(buildCode);
		}

		if (Boolean.parseBoolean(env.getProperty("toolkit.deploy", "false")) && buildSuccessful) {
			LOG.info("Deployment will be started for buildCode: " + buildCode);
			deploymentCode = createDeployment(buildCode);
		}

		if (StringUtils.hasText(deploymentCode)) {
			LOG.info("Deployment progress will be watched: " + deploymentCode);
			deploymentSuccessful = watchDeploymentProgress(deploymentCode);
		}
	}

	private String createBuild() {
		String applicationCode = env.getProperty("toolkit.build.applicationCode", "");
		String buildBranch = env.getProperty("toolkit.build.branch", "develop");
		String buildName = env.getProperty("toolkit.build.name", "develop-" + LocalDate.now());

		return cloudBuildService.createBuild(applicationCode, buildBranch, buildName);
	}

	private String createDeployment(String buildCode) {
		CloudEnvironment deployEnvironment = CloudEnvironment.valueOf(env.getProperty("toolkit.deploy.environment", "d1"));
		DatabaseUpdateMode dbUpdateMode = DatabaseUpdateMode.valueOf(env.getProperty("toolkit.deploy.dbUpdateMode", "NONE"));
		DeployStrategy deployStrategy = DeployStrategy.valueOf(env.getProperty("toolkit.deploy.strategy", "ROLLING_UPDATE"));

		DeploymentRequestDTO deploymentRequestDTO = cloudDeploymentService.createDeploymentRequestDTO(buildCode,
				dbUpdateMode, deployEnvironment, deployStrategy);

		return cloudDeploymentService.createDeployment(deploymentRequestDTO);
	}

	private boolean watchBuildProgress(final String buildCode) {
		try {
			return cloudBuildService.handleBuildProgress(buildCode);
		} catch (InterruptedException ie) {
			LOG.error("Error during build watching progress", ie);
			return false;
		}
	}

	private boolean watchDeploymentProgress(final String deploymentCode) {
		try {
			return cloudDeploymentService.handleDeploymentProgress(deploymentCode);
		} catch (InterruptedException ie) {
			LOG.error("Error during deployment watching progress", ie);
			return false;
		}
	}

	private boolean hasInvalidPropertiesAndArguments() {
		LOG.info("Verify properties / arguments");
		boolean isInvalid = false;
		if (!StringUtils.hasText(env.getProperty("toolkit.subscriptionCode"))) {
			LOG.error("No subscription code given");
			isInvalid = true;
		}

		if (!StringUtils.hasText(env.getProperty("toolkit.apiKey"))) {
			LOG.error("No API key given");
			isInvalid = true;
		}

		try {
			String deployEnvironment = env.getProperty("toolkit.deploy.environment");
			String dbUpdateMode = env.getProperty("toolkit.deploy.dbUpdateMode");
			String deployStrategy = env.getProperty("toolkit.deploy.strategy");

			if (StringUtils.hasText(deployEnvironment)) {
				CloudEnvironment.valueOf(deployEnvironment);
			}

			if (StringUtils.hasText(dbUpdateMode)) {
				DatabaseUpdateMode.valueOf(dbUpdateMode);
			}

			if (StringUtils.hasText(deployStrategy)) {
				DeployStrategy.valueOf(deployStrategy);
			}

		} catch (IllegalArgumentException iae) {
			LOG.error("Illegal deployment arguments given", iae);
			isInvalid = true;
		}

		return isInvalid;
	}
}
