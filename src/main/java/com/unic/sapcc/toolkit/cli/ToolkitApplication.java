package com.unic.sapcc.toolkit.cli;

import com.unic.sapcc.toolkit.dto.BuildRequestDTO;
import com.unic.sapcc.toolkit.dto.DeploymentRequestDTO;
import com.unic.sapcc.toolkit.enums.CloudEnvironment;
import com.unic.sapcc.toolkit.enums.DatabaseUpdateMode;
import com.unic.sapcc.toolkit.enums.DeployStrategy;
import com.unic.sapcc.toolkit.services.CloudBuildService;
import com.unic.sapcc.toolkit.services.CloudDeploymentService;
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
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;

@Profile("!test")
@SpringBootApplication
@ComponentScan(basePackages = "com.unic.sapcc.toolkit")
public class ToolkitApplication implements CommandLineRunner {

	public static final String SHORTOPTION_BUILD = "b";
	public static final String SHORTOPTION_ASYNC = "y";
	public static final String SHORTOPTION_DEPLOY = "d";
	public static final String SHORTOPTION_BUILDCODE = "c";
	public static final String SHOROPTION_APPCODE = "a";
	public static final String SHORTOPTION_BRANCH = "r";
	public static final String SHORTOPTION_BUILDNAME = "n";
	public static final String SHORTOPTION_ENV = "e";
	public static final String SHORTOPTION_MODE = "u";
	public static final String SHORTOPTION_STRATEGY = "s";
	private static final Logger LOG = LoggerFactory.getLogger(ToolkitApplication.class);
	private static final String SHORTOPTION_HELP = "h";
	private static final String SHORTOPTION_PIDFILE = "p";
	private static final String SHORTOPTION_SKIPBUILDTIMEOUTS = "t";

	@Autowired
	public CloudBuildService cloudBuildService;

	@Autowired
	public CloudDeploymentService cloudDeploymentService;

	@Autowired
	private ConfigurableApplicationContext applicationContext;

	public static void main(String[] args) {
		SpringApplication.run(ToolkitApplication.class, args);
	}

	private static Options buildOptions() {
		Options options = new Options();
		options.addOption(SHORTOPTION_HELP, "help", false, "print usage help");

		options.addOption(SHORTOPTION_BUILD, "build", false, "Execute build");
		options.addOption(SHORTOPTION_ASYNC, "async", false, "Don't monitor progress");
		options.addOption(SHORTOPTION_DEPLOY, "deploy", false, "Execute deployment");
		options.addOption(SHORTOPTION_BUILDCODE, "buildcode", true, "Code of build to deploy");

		options.addOption(SHORTOPTION_ENV, "environment", true, "environment for deployment");
		options.addOption(SHORTOPTION_MODE, "updatemode", true, "database update mode for deployment");
		options.addOption(SHORTOPTION_STRATEGY, "strategy", true, "deployment strategy");

		options.addOption(SHOROPTION_APPCODE, "applicationcode", true, "application code");
		options.addOption(SHORTOPTION_BRANCH, "branch", true, "branch to be build");
		options.addOption(SHORTOPTION_BUILDNAME, "name", true, "build name");

		options.addOption(SHORTOPTION_PIDFILE, "pidfile", true, "process id file");
		options.addOption(SHORTOPTION_SKIPBUILDTIMEOUTS, "skipBuildTimeouts", false, "skip build timeouts during build monitoring");
		return options;
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

			if (cmd.hasOption(SHORTOPTION_ASYNC)) {
				LOG.info("Detected ASYNC option, ignoring any further commands.");
				return;
			}
			watchBuildProgress(buildCode, cmd.hasOption(SHORTOPTION_SKIPBUILDTIMEOUTS));
		}

		if (cmd.hasOption(SHORTOPTION_DEPLOY)) {
			if (!cmd.hasOption(SHORTOPTION_BUILD)) {
				buildCode = cmd.getOptionValue(SHORTOPTION_BUILDCODE);
				watchBuildProgress(buildCode, cmd.hasOption(SHORTOPTION_SKIPBUILDTIMEOUTS));
			}
			String deploymentCode = createDeployment(buildCode, cmd);

			if (cmd.hasOption(SHORTOPTION_ASYNC)) {
				LOG.info("Detected ASYNC option, ignoring any further commands.");
				return;
			}
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

	private String createBuild(CommandLine cmd) {
		LOG.info("Build will be started");

		String applicationCode = cmd.getOptionValue(SHOROPTION_APPCODE, "");
		String buildBranch = cmd.getOptionValue(SHORTOPTION_BRANCH, "develop");
		String buildName = cmd.getOptionValue(SHORTOPTION_BUILDNAME, buildBranch.replaceAll("/", "_") + "-" + LocalDate.now());
		BuildRequestDTO buildRequestDTO = new BuildRequestDTO(applicationCode, buildBranch, buildName);

		String buildId = cloudBuildService.createBuild(buildRequestDTO);

		writeProcessIdToFile(cmd.getOptionValue(SHORTOPTION_PIDFILE), buildId);

		return buildId;
	}

	private String createDeployment(String buildCode, CommandLine cmd) {
		CloudEnvironment deployEnvironment = CloudEnvironment.valueOf(cmd.getOptionValue(SHORTOPTION_ENV, "d1"));
		DatabaseUpdateMode dbUpdateMode = DatabaseUpdateMode.valueOf(cmd.getOptionValue(SHORTOPTION_MODE, "NONE"));
		DeployStrategy deployStrategy = DeployStrategy.valueOf(cmd.getOptionValue(SHORTOPTION_STRATEGY, "ROLLING_UPDATE"));

		DeploymentRequestDTO deploymentRequestDTO = cloudDeploymentService.createDeploymentRequestDTO(buildCode, dbUpdateMode,
				deployEnvironment, deployStrategy);

		return cloudDeploymentService.createDeployment(deploymentRequestDTO);
	}

	private void watchBuildProgress(final String buildCode, final boolean skipBuildTimeouts) {
		LOG.info("Build progress will be watched: " + buildCode);
		try {
			cloudBuildService.handleBuildProgress(buildCode, skipBuildTimeouts);
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

	private void writeProcessIdToFile(String filePath, String id) {
		if (!StringUtils.hasText(filePath)) {
			LOG.debug("Not writing process ID to to empty path");
			return;
		}
		Path path = Paths.get(filePath);
		try {
			LOG.info("writing process ID to " + path.toAbsolutePath());
			Files.writeString(path, id);
		} catch (IOException e) {
			LOG.error("Failed to write out process Id!", e);
		}
	}
}
