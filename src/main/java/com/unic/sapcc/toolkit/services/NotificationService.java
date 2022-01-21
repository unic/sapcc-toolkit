package com.unic.sapcc.toolkit.services;

/**
 * Interface for all notification services.
 * Make sure to implement this interface and define a Condition when your preferred notification service should be taken
 */
public interface NotificationService {

	/**
	 * Send the message as DTO through your notification provider.
	 *
	 * @param dto
	 * @param <T>
	 */
	<T> void sendMessage(T dto);
}
