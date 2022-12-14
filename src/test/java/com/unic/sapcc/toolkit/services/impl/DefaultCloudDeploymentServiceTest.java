package com.unic.sapcc.toolkit.services.impl;

import com.unic.sapcc.toolkit.config.ToolkitConfig;
import com.unic.sapcc.toolkit.dto.DeploymentHistoryListResponseDTO;
import com.unic.sapcc.toolkit.dto.DeploymentProgressDTO;
import com.unic.sapcc.toolkit.dto.DeploymentRequestDTO;
import com.unic.sapcc.toolkit.dto.DeploymentResponseDTO;
import com.unic.sapcc.toolkit.enums.CloudEnvironment;
import com.unic.sapcc.toolkit.enums.DatabaseUpdateMode;
import com.unic.sapcc.toolkit.enums.DeployStrategy;
import com.unic.sapcc.toolkit.enums.DeploymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.net.SocketTimeoutException;
import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultCloudDeploymentServiceTest {
	private final static String fakeSubscriptionId = "abcd1234";
	private final static String baseurl = "https://portalrotapi.hana.ondemand.com/v2/subscriptions/" + fakeSubscriptionId;
	@Spy
	private final RetryTemplate retryTemplate = new ToolkitConfig().retryRemoteAccessExceptionTemplate();
	@Mock
	private Environment env;
	@Mock
	private RestTemplate restTemplate;
	@Captor
	private ArgumentCaptor<HttpEntity<DeploymentRequestDTO>> entityCaptor;
	@InjectMocks
	private DefaultCloudDeploymentService unitUnderTest;

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
				.thenReturn(new ResponseEntity<>(deploymentResponseDTO, HttpStatus.OK));

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
				.thenReturn(new ResponseEntity<>(deploymentResponseDTO, HttpStatus.OK));

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

		assertEquals(buildCode, deploymentRequestDTO.buildCode());
		assertEquals(databaseUpdateMode, deploymentRequestDTO.databaseUpdateMode());
		assertEquals(deployEnvironmentCode, deploymentRequestDTO.environmentCode());
		assertEquals(deployStrategy, deploymentRequestDTO.strategy());
	}

	@Test
	void handleDeploymentProgress() throws Exception {
		final String deploymentCode = "fakeDeployCode";
		final String url = baseurl + "/deployments/" + deploymentCode + "/progress";

		final DeploymentProgressDTO deploymentProgressDTO = new DeploymentProgressDTO("", "", DeploymentStatus.DEPLOYED, 0, null);

		when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(DeploymentProgressDTO.class))).thenReturn(
				new ResponseEntity<>(deploymentProgressDTO, HttpStatus.OK));
		when(env.getProperty(eq("toolkit.deploy.sleepTime"), anyString())).thenReturn("5");
		when(env.getProperty(eq("toolkit.deploy.maxWaitTime"), anyString())).thenReturn("1");

		unitUnderTest.handleDeploymentProgress(deploymentCode);

		verify(restTemplate).exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(DeploymentProgressDTO.class));
	}

	@Test
	void waitForDeploymentClearance() throws Exception {
		final String url = baseurl + "/deployments";
		final DeploymentHistoryListResponseDTO deploymentHistory= new DeploymentHistoryListResponseDTO(Collections.EMPTY_LIST);

		when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(DeploymentHistoryListResponseDTO.class))).thenReturn(
				new ResponseEntity<>(deploymentHistory, HttpStatus.OK));
		when(env.getProperty(eq("toolkit.deploy.sleepTime"), anyString())).thenReturn("5");
		when(env.getProperty(eq("toolkit.deploy.maxWaitTime"), anyString())).thenReturn("1");

		unitUnderTest.waitForDeploymentClearance(CloudEnvironment.d1);

		verify(restTemplate).exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(DeploymentHistoryListResponseDTO.class));
	}

	@Test
	void handleDeploymentProgressException() throws Exception {
		final String deploymentCode = "fakeDeployCode";
		final String url = baseurl + "/deployments/" + deploymentCode + "/progress";

		final DeploymentProgressDTO buildProgressDTOOk = new DeploymentProgressDTO("", "", DeploymentStatus.DEPLOYED, 0, null);
		final DeploymentProgressDTO buildProgressDTOWait = new DeploymentProgressDTO("", "", DeploymentStatus.DEPLOYING, 0, null);

		when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(DeploymentProgressDTO.class)))
				.thenReturn(new ResponseEntity<>(buildProgressDTOWait, HttpStatus.OK))
				.thenThrow(new ResourceAccessException("Fail", new SocketTimeoutException("Mocking timeout")))
				.thenReturn(new ResponseEntity<>(buildProgressDTOOk, HttpStatus.OK));
		when(env.getProperty(eq("toolkit.deploy.sleepTime"), anyString())).thenReturn("5");
		when(env.getProperty(eq("toolkit.deploy.maxWaitTime"), anyString())).thenReturn("30");

		unitUnderTest.handleDeploymentProgress(deploymentCode);

		verify(restTemplate, times(3))
				.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(DeploymentProgressDTO.class));
	}
}
