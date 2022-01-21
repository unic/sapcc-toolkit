package com.unic.sapcc.toolkit.cli;

import com.unic.sapcc.toolkit.dto.BuildRequestDTO;
import com.unic.sapcc.toolkit.dto.DeploymentRequestDTO;
import com.unic.sapcc.toolkit.services.impl.DefaultCloudBuildService;
import com.unic.sapcc.toolkit.services.impl.DefaultCloudDeploymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static com.unic.sapcc.toolkit.enums.CloudEnvironment.d1;
import static com.unic.sapcc.toolkit.enums.DatabaseUpdateMode.NONE;
import static com.unic.sapcc.toolkit.enums.DeployStrategy.ROLLING_UPDATE;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ToolkitApplicationTests {
	private static final String DEPLOYMENT_CODE = "foobar";
	private static final String BUILD_CODE = "booyakka";
	private static final String APPCODE = "dummyAppcode";
	private static final String BRANCH_NAME = "dummyBranchName";
	private static final String BUILD_NAME = "dummyBuildName";
	@InjectMocks
	ToolkitApplication unitUnderTest = new ToolkitApplication();
	@Mock
	private DefaultCloudBuildService cloudBuildService;
	@Mock
	private DefaultCloudDeploymentService cloudDeploymentService;
	@Mock
	private ConfigurableApplicationContext applicationContext;

	@Test
	void testFullParameterSet() throws Exception {
		String[] args = { "-b", "-a", APPCODE, "-r", BRANCH_NAME, "-n", BUILD_NAME, "-d", "-s", ROLLING_UPDATE.name(), "-u",
				NONE.name(), "-e", d1.name() };
		DeploymentRequestDTO deploymentRequest = new DeploymentRequestDTO(BUILD_CODE, NONE, d1, ROLLING_UPDATE);
		BuildRequestDTO buildRequestDTO = new BuildRequestDTO(APPCODE, BRANCH_NAME, BUILD_NAME);

		Mockito.when(cloudBuildService.createBuild(buildRequestDTO)).thenReturn(BUILD_CODE);
		Mockito.when(cloudDeploymentService.createDeploymentRequestDTO(BUILD_CODE, NONE, d1, ROLLING_UPDATE))
				.thenReturn(deploymentRequest);
		Mockito.when(cloudDeploymentService.createDeployment(deploymentRequest)).thenReturn(DEPLOYMENT_CODE);
		unitUnderTest.setNotificationService(null);
		unitUnderTest.run(args);

		Mockito.verify(cloudBuildService).handleBuildProgress(BUILD_CODE);

		Mockito.verify(cloudDeploymentService).handleDeploymentProgress(DEPLOYMENT_CODE);
	}

	@Test
	void testBuildOnly() throws Exception {
		String[] args = { "-b", "-a", APPCODE, "-n", BUILD_NAME };

		BuildRequestDTO buildRequestDTO = new BuildRequestDTO(APPCODE, "develop", BUILD_NAME);
		Mockito.when(cloudBuildService.createBuild(buildRequestDTO)).thenReturn(BUILD_CODE);
		unitUnderTest.setNotificationService(null);
		unitUnderTest.run(args);

		Mockito.verify(cloudBuildService).handleBuildProgress(BUILD_CODE);

		Mockito.verifyNoInteractions(cloudDeploymentService);
	}

	@Test
	void testDeployOnly() throws Exception {
		String[] args = { "-d", "-c", BUILD_CODE, "-s", ROLLING_UPDATE.name(), "-u", NONE.name(), "-e", d1.name() };

		DeploymentRequestDTO deploymentRequest = new DeploymentRequestDTO(BUILD_CODE, NONE, d1, ROLLING_UPDATE);
		Mockito.when(cloudDeploymentService.createDeploymentRequestDTO(BUILD_CODE, NONE, d1, ROLLING_UPDATE))
				.thenReturn(deploymentRequest);
		Mockito.when(cloudDeploymentService.createDeployment(deploymentRequest)).thenReturn(DEPLOYMENT_CODE);
		unitUnderTest.setNotificationService(null);
		unitUnderTest.run(args);

		Mockito.verify(cloudBuildService).handleBuildProgress(BUILD_CODE);

		Mockito.verify(cloudDeploymentService).handleDeploymentProgress(DEPLOYMENT_CODE);

		Mockito.verifyNoMoreInteractions(cloudBuildService);
	}
}
