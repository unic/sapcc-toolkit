package com.unic.sapcc.toolkit.services;

import com.unic.sapcc.toolkit.dto.BuildRequestDTO;

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
	String createBuild(String applicationCode, String branch, String name);

	/**
	 * Create a new build for the build parameters wrapped in BuildRequestDTO.
	 *
	 * @param buildRequestDTO
	 * @return the newly created buildCode as String
	 */
	String createBuild(BuildRequestDTO buildRequestDTO);

	/**
	 * Monitor the build progress for the given buildCode for a maximum time of ${toolkit.build.maxWaitTime}.
	 * Progress will be updated for the given interval ${toolkit.build.sleepTime}.
	 *
	 * @param buildCode
	 * @return true, if build progress is finished successfully, otherwise false
	 * @throws InterruptedException
	 * @throws IllegalStateException
	 */
	void handleBuildProgress(String buildCode) throws InterruptedException, IllegalStateException;
}
