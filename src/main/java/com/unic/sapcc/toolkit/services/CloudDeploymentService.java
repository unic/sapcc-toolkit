package com.unic.sapcc.toolkit.services;

import com.unic.sapcc.toolkit.dto.DeploymentRequestDTO;
import com.unic.sapcc.toolkit.enums.CloudEnvironment;
import com.unic.sapcc.toolkit.enums.DatabaseUpdateMode;
import com.unic.sapcc.toolkit.enums.DeployStrategy;

/**
 * CloudBuildService provides methods to start and watch progress of deployments in the SAP Commerce Cloud
 */
public interface CloudDeploymentService {

	/**
	 * Create a new deployment for the given DeploymentRequestDTO.
	 *
	 * @param deploymentRequestDTO
	 * @return the newly created deploymentCode
	 */
	public String createDeployment(DeploymentRequestDTO deploymentRequestDTO);

	/**
	 * Create a new DeploymentRequestDTO which wraps important deployment parameters needed to create a new deployment.
	 * @param buildCode
	 * @param deployDatabaseUpdateMode
	 * @param deployEnvironmentCode
	 * @param deployStrategy
	 * @return
	 */
	public DeploymentRequestDTO createDeploymentRequestDTO(String buildCode, DatabaseUpdateMode deployDatabaseUpdateMode,
			CloudEnvironment deployEnvironmentCode, DeployStrategy deployStrategy);

	/**
	 * Monitor the deployment progress for the given deploymentCode for a maximum time of ${toolkit.deploy.maxWaitTime}.
	 * Progress will be updated for the given interval ${toolkit.deploy.sleepTime}.
	 * @param deploymentCode
	 * @throws InterruptedException
	 * @throes IllegalStateException
	 */
	public void handleDeploymentProgress(String deploymentCode) throws InterruptedException, IllegalStateException;
}
