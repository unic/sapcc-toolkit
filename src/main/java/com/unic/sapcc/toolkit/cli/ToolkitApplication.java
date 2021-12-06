package com.unic.sapcc.toolkit.cli;

import com.unic.sapcc.toolkit.dto.DeploymentRequestDTO;
import com.unic.sapcc.toolkit.enums.CloudEnvironment;
import com.unic.sapcc.toolkit.enums.DatabaseUpdateMode;
import com.unic.sapcc.toolkit.enums.DeployStrategy;
import com.unic.sapcc.toolkit.services.impl.DefaultCloudBuildService;
import com.unic.sapcc.toolkit.services.impl.DefaultCloudDeploymentService;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.LocalDate;

@Profile("!test")
@SpringBootApplication
@ComponentScan(basePackages = "com.unic.sapcc.toolkit")
public class ToolkitApplication implements CommandLineRunner {

	private static final Logger LOG = LoggerFactory.getLogger(ToolkitApplication.class);
	public static final String SHORTOPTION_BUILD = "b";
	public static final String SHORTOPTION_DEPLOY = "d";
	public static final String SHORTOPTION_BUILDCODE = "c";
	public static final String SHOROPTION_APPCODE = "a";
	public static final String SHORTOPTION_BRANCH = "r";
	public static final String SHORTOPTION_BUILDNAME = "n";
	public static final String SHORTOPTION_ENV = "e";
	public static final String SHORTOPTION_MODE = "u";
	public static final String SHORTOPTION_STRATEGY = "s";
	private static final String SHORTOPTION_HELP = "h";

	@Autowired
	public DefaultCloudBuildService cloudBuildService;

	@Autowired
	public DefaultCloudDeploymentService cloudDeploymentService;

	@Autowired
	public Environment env;

	@Autowired
	private ConfigurableApplicationContext applicationContext;

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.setConnectTimeout(Duration.ofMillis(3000)).setReadTimeout(Duration.ofMillis(3000)).build();
	}

	public static void main(String[] args) {
		SpringApplication.run(ToolkitApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		CommandLine cmd = getCommandLine(buildOptions(), args);

		if (!(cmd.hasOption(SHORTOPTION_BUILD) || cmd.hasOption(SHORTOPTION_DEPLOY))) {
			LOG.error("No action selected! Start the tool with either the '-" + SHORTOPTION_BUILD + "' or  '-" + SHORTOPTION_DEPLOY
					+ "' flag.");
		}

		String buildCode = null;
		if (cmd.hasOption(SHORTOPTION_BUILD)) {
			buildCode = createBuild(cmd);
			watchBuildProgress(buildCode);
		}

		if (cmd.hasOption(SHORTOPTION_DEPLOY)) {
			if (!cmd.hasOption(SHORTOPTION_BUILD)) {
				buildCode = cmd.getOptionValue(SHORTOPTION_BUILDCODE);
				watchBuildProgress(buildCode);
			}
			String deploymentCode = createDeployment(buildCode, cmd);
			watchDeploymentProgress(deploymentCode);
		}

		applicationContext.close();
	}

	private CommandLine getCommandLine(final Options options, final String[] args) throws ParseException {
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
			if (args.length == 0 || cmd.hasOption(SHORTOPTION_HELP)) {
				printHelp(options);
				System.exit(0);
			}
		} catch (MissingOptionException moe) {
			printHelp(options);
			System.err.println(moe.getMessage());
			System.exit(1);
		}
		return cmd;
	}

	private void printHelp(final Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("sapcc-toolkit", options);
	}

	private static Options buildOptions() {
		Options options = new Options();
		options.addOption(SHORTOPTION_HELP, "help", false, "print usage help");

		options.addOption(SHORTOPTION_BUILD, "build", false, "Execute build");
		options.addOption(SHORTOPTION_DEPLOY, "deploy", false, "Execute deployment");
		options.addOption(SHORTOPTION_BUILDCODE, "buildcode", true, "Code of build to deploy");

		options.addOption(SHORTOPTION_ENV, "environment", true, "envrionment for deployment");
		options.addOption(SHORTOPTION_MODE, "updatemode", true, "database update mode for deployment");
		options.addOption(SHORTOPTION_STRATEGY, "strategy", true, "deployment strategy");

		options.addOption(SHOROPTION_APPCODE, "applicationcode", true, "application code");
		options.addOption(SHORTOPTION_BRANCH, "branch", true, "brannch to be build");
		options.addOption(SHORTOPTION_BUILDNAME, "name", true, "build name");
		return options;
	}

	private String createBuild(CommandLine cmd) {
		LOG.info("Build will be started");

		String applicationCode = cmd.getOptionValue(SHOROPTION_APPCODE, "");
		String buildBranch = cmd.getOptionValue(SHORTOPTION_BRANCH, "develop");
		String buildName = cmd.getOptionValue(SHORTOPTION_BUILDNAME, "develop-" + LocalDate.now());

		return cloudBuildService.createBuild(applicationCode, buildBranch, buildName);
	}

	private String createDeployment(String buildCode, CommandLine cmd) {
		CloudEnvironment deployEnvironment = CloudEnvironment.valueOf(cmd.getOptionValue(SHORTOPTION_ENV, "d1"));
		DatabaseUpdateMode dbUpdateMode = DatabaseUpdateMode.valueOf(cmd.getOptionValue(SHORTOPTION_MODE, "NONE"));
		DeployStrategy deployStrategy = DeployStrategy.valueOf(cmd.getOptionValue(SHORTOPTION_STRATEGY, "ROLLING_UPDATE"));

		DeploymentRequestDTO deploymentRequestDTO = cloudDeploymentService.createDeploymentRequestDTO(buildCode, dbUpdateMode,
				deployEnvironment, deployStrategy);

		return cloudDeploymentService.createDeployment(deploymentRequestDTO);
	}

	private void watchBuildProgress(final String buildCode) {
		LOG.info("Build progress will be watched: " + buildCode);
		try {
			cloudBuildService.handleBuildProgress(buildCode);
		} catch (InterruptedException | IllegalStateException e) {
			LOG.error("Error during build watching progress", e);
			System.exit(1);
		}
	}

	private void watchDeploymentProgress(final String deploymentCode) {
		try {
			cloudDeploymentService.handleDeploymentProgress(deploymentCode);
		} catch (InterruptedException | IllegalStateException e) {
			LOG.error("Error during deployment watching progress", e);
			System.exit(1);
		}
	}
}
