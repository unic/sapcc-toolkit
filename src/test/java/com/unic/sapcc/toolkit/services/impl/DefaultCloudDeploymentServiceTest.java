package com.unic.sapcc.toolkit.services.impl;

import com.unic.sapcc.toolkit.dto.DeploymentProgressDTO;
import com.unic.sapcc.toolkit.dto.DeploymentRequestDTO;
import com.unic.sapcc.toolkit.dto.DeploymentResponseDTO;
import com.unic.sapcc.toolkit.enums.CloudEnvironment;
import com.unic.sapcc.toolkit.enums.DatabaseUpdateMode;
import com.unic.sapcc.toolkit.enums.DeployStrategy;
import com.unic.sapcc.toolkit.enums.DeploymentStatus;
import com.unic.sapcc.toolkit.services.CloudDeploymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultCloudDeploymentServiceTest {
	@Mock
	private Environment env;

	@Mock
	private RestTemplate restTemplate;

	@Captor
	private ArgumentCaptor<HttpEntity<DeploymentRequestDTO>> entityCaptor;

	@InjectMocks
	CloudDeploymentService unitUnderTest = new DefaultCloudDeploymentService();

	private final static String fakeSubscriptionId = "abcd1234";
	private final static String baseurl = "https://portalrotapi.hana.ondemand.com/v2/subscriptions/" + fakeSubscriptionId;

	@BeforeEach
	public void beforeEach() {
		ReflectionTestUtils.setField(unitUnderTest, "subscriptionCode", fakeSubscriptionId);
	}

	@Test
	void createDeployment_ValidateInputMapping() {
		String code = "BOOYAKKA!";
		DeploymentResponseDTO deploymentResponseDTO = new DeploymentResponseDTO(fakeSubscriptionId, code);
		DeploymentRequestDTO dto = new DeploymentRequestDTO("fakeDeployment", DatabaseUpdateMode.NONE, CloudEnvironment.d1,
				DeployStrategy.ROLLING_UPDATE);
		String url = baseurl + "/deployments";

		when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class), eq(DeploymentResponseDTO.class)))
				.thenReturn(new ResponseEntity(deploymentResponseDTO, HttpStatus.OK));

		unitUnderTest.createDeployment(dto);

		verify(restTemplate).exchange(eq(url), eq(HttpMethod.POST), entityCaptor.capture(), eq(DeploymentResponseDTO.class));

		assertEquals(dto, entityCaptor.getValue().getBody());
	}

	@Test
	void createDeployment_Success() {
		String code = "BOOYAKKA!";
		DeploymentResponseDTO deploymentResponseDTO = new DeploymentResponseDTO(fakeSubscriptionId, code);
		DeploymentRequestDTO dto = new DeploymentRequestDTO("fakeDeployment", DatabaseUpdateMode.NONE, CloudEnvironment.d1,
				DeployStrategy.ROLLING_UPDATE);

		when(restTemplate.exchange(eq(baseurl + "/deployments"), eq(HttpMethod.POST), any(HttpEntity.class),
				eq(DeploymentResponseDTO.class)))
				.thenReturn(new ResponseEntity(deploymentResponseDTO, HttpStatus.OK));

		String result = unitUnderTest.createDeployment(dto);
		assertEquals(code, result);
	}

	@Test
	void createDeploymentRequestDTO() {
		final String buildCode = "fakeCode";
		final DatabaseUpdateMode databaseUpdateMode = DatabaseUpdateMode.INITIALIZE;
		final CloudEnvironment deployEnvironmentCode = CloudEnvironment.s1;
		final DeployStrategy deployStrategy = DeployStrategy.RECREATE;

		DeploymentRequestDTO deploymentRequestDTO = unitUnderTest.createDeploymentRequestDTO(buildCode, databaseUpdateMode,
				deployEnvironmentCode, deployStrategy);

		assertEquals(buildCode, deploymentRequestDTO.getBuildCode());
		assertEquals(databaseUpdateMode, deploymentRequestDTO.getDatabaseUpdateMode());
		assertEquals(deployEnvironmentCode, deploymentRequestDTO.getEnvironmentCode());
		assertEquals(deployStrategy, deploymentRequestDTO.getStrategy());
	}

	@Test
	void handleDeploymentProgress() throws Exception {
		final String deploymentCode = "fakeDeployCode";
		final String url = baseurl + "/deployments/" + deploymentCode + "/progress";

		final DeploymentProgressDTO buildProgressDTO = new DeploymentProgressDTO("", "", DeploymentStatus.DEPLOYED, 0, null);

		when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(DeploymentProgressDTO.class))).thenReturn(
				new ResponseEntity(buildProgressDTO, HttpStatus.OK));
		when(env.getProperty(eq("toolkit.deploy.sleepTime"), anyString())).thenReturn("5");
		when(env.getProperty(eq("toolkit.deploy.maxWaitTime"), anyString())).thenReturn("30");

		unitUnderTest.handleDeploymentProgress(deploymentCode);
	}
}