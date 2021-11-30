package com.unic.sapcc.toolkit.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
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

import com.unic.sapcc.toolkit.dto.BodyDTO;
import com.unic.sapcc.toolkit.dto.BuildProgressDTO;
import com.unic.sapcc.toolkit.dto.BuildRequestDTO;
import com.unic.sapcc.toolkit.dto.BuildResponseDTO;
import com.unic.sapcc.toolkit.enums.BuildStatus;
import com.unic.sapcc.toolkit.services.CloudBuildService;

@ExtendWith(MockitoExtension.class)
class DefaultCloudBuildServiceTest
{
	@Mock
	private Environment env;

	@Mock
	private RestTemplate restTemplate;

	@Captor
	private ArgumentCaptor<HttpEntity<BodyDTO>> entityCaptor;

	@InjectMocks
	private CloudBuildService unitUnderTest = new DefaultCloudBuildService();

	private final static String fakeSubscriptionId = "abcd1234";
	private final static String baseurl = "https://portalrotapi.hana.ondemand.com/v2/subscriptions/" + fakeSubscriptionId;


	@BeforeEach
	public void beforeEach()
	{
		ReflectionTestUtils.setField(unitUnderTest, "subscriptionCode", fakeSubscriptionId);
	}

	@Test
	void createBuild_verifyParameterPassthrough()
	{
		String appCode = "fakeAppCode";
		String branch = "fakeBranch";
		String name = "fakeName";
		String url = "https://portalrotapi.hana.ondemand.com/v2/subscriptions/" + fakeSubscriptionId+"/builds";

		when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class), eq(BuildResponseDTO.class))).thenReturn(
				new ResponseEntity(null, HttpStatus.OK));

		unitUnderTest.createBuild(appCode, branch, name);

		verify(restTemplate).exchange(eq(url), eq(HttpMethod.POST), entityCaptor.capture(), eq(BuildResponseDTO.class));

		BuildRequestDTO body = (BuildRequestDTO) entityCaptor.getValue().getBody();
		assertEquals(appCode, body.getApplicationCode());
		assertEquals(branch, body.getBranch());
		assertEquals(name, body.getName());
	}

	@Test
	void createBuild_verifyInstantSuccess()
	{
		String buildCode = "fakeCode";
		String url = baseurl + "/builds";

		BuildResponseDTO buildResponseDTO = new BuildResponseDTO(fakeSubscriptionId, buildCode);

		when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class), eq(BuildResponseDTO.class))).thenReturn(
				new ResponseEntity(buildResponseDTO, HttpStatus.OK));

		String response = unitUnderTest.createBuild("", "", "");
		assertEquals(buildCode, response);
	}

	/*
	 * TODO: Does this make sense? In this case we want to start a build but do not get a build Code back. Is this a valid use case or should we throw an exception here?
	 */
	@Test
	void createBuild_noResponseBody()
	{
		String url = "https://portalrotapi.hana.ondemand.com/v2/subscriptions/" + fakeSubscriptionId + "/builds";

		when(restTemplate.exchange(eq(url), eq(HttpMethod.POST), any(HttpEntity.class), eq(BuildResponseDTO.class))).thenReturn(
				new ResponseEntity(null, HttpStatus.OK));

		String response = unitUnderTest.createBuild("", "", "");
		assertNull(response);
	}


	static class BuildProgressProvider implements ArgumentsProvider
	{
		private BuildProgressDTO buildBuildProgressDto(final String status)
		{
			return new BuildProgressDTO(null, null, null, 0, 0, status, null);
		}

		@Override
		public Stream<? extends Arguments> provideArguments(final ExtensionContext context) throws Exception
		{
			return Stream.of(Arguments.of(buildBuildProgressDto("SUCCESS"), BuildStatus.SUCCESS),
					Arguments.of(buildBuildProgressDto("BUILDING"), BuildStatus.BUILDING),
					Arguments.of(buildBuildProgressDto("ERROR"), BuildStatus.ERROR),
					Arguments.of(buildBuildProgressDto("UNDEFINED"), BuildStatus.UNKNOWN),
					Arguments.of(buildBuildProgressDto("UNKNOWN"), BuildStatus.UNKNOWN));
		}
	}

	@ParameterizedTest
	@ArgumentsSource(BuildProgressProvider.class)
	void verifyBuildProgress(BuildProgressDTO buildProgressDTO, BuildStatus expected)
	{
		assertEquals(expected, unitUnderTest.verifyBuildProgress(buildProgressDTO));
	}

	@Test
	void handleBuildProgress_Success() throws Exception
	{

		final String buildCode = "fakeBuildCode";
		final String url = baseurl + "/builds/" + buildCode + "/progress";

		final BuildProgressDTO buildProgressDTO = new BuildProgressDTO("", "", "", 0, 0, "SUCCESS", null);

		when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(BuildProgressDTO.class))).thenReturn(
				new ResponseEntity(buildProgressDTO, HttpStatus.OK));
		when(env.getProperty(eq("toolkit.build.sleepTime"), anyString())).thenReturn("5");
		when(env.getProperty(eq("toolkit.build.maxWaitTime"), anyString())).thenReturn("30");


		boolean result = unitUnderTest.handleBuildProgress(buildCode);

		assertTrue(result);
	}
}