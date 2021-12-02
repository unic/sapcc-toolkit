package com.unic.sapcc.toolkit.services;

import com.unic.sapcc.toolkit.dto.BuildProgressDTO;
import com.unic.sapcc.toolkit.dto.BuildRequestDTO;
import com.unic.sapcc.toolkit.enums.BuildStatus;

/**
 * CloudBuildService provides methods to start and watch progress of builds in the SAP Commerce Cloud
 */
public interface CloudBuildService {

	/**
	 * Create a new build for the given parameters.
	 *
	 * @param applicationCode
	 * @param branch
	 * @param name
	 * @return the newly created buildCode as String
	 */
	public String createBuild(String applicationCode, String branch, String name);

	/**
	 * Create a new build for the build parameters wrapped in BuildRequestDTO.
	 *
	 * @param buildRequestDTO
	 * @return the newly created buildCode as String
	 */
	public String createBuild(BuildRequestDTO buildRequestDTO);

	/**
	 * Verify the build progress for the build parameters wrapped in BuildProgressDTO.
	 *
	 * @param buildProgressDTO
	 * @return the current BuildStatus as enum
	 */
	public BuildStatus verifyBuildProgress(BuildProgressDTO buildProgressDTO);

	/**
	 * Monitor the build progress for the given buildCode for a maximum time of ${toolkit.build.maxWaitTime}.
	 * Progress will be updated for the given interval ${toolkit.build.sleepTime}.
	 *
	 * @param buildCode
	 * @return true, if build progress is finished successfully, otherwise false
	 * @throws InterruptedException
	 * @throws IllegalStateException
	 */
	public void handleBuildProgress(String buildCode) throws InterruptedException, IllegalStateException;
}
