package com.unic.sapcc.toolkit.services;

/**
 * Interface for all notification services.
 * Make sure to implement this interface and define a Condition when your preferred notification service should be taken
 */
public interface NotificationService {

	/**
	 * The message you want to send through you notification provider.
	 *
	 * @param message
	 */
	void sendMessage(String message);

	/**
	 * Format the DTO Object in a readable structure for your notification provider.
	 *
	 * @param dto
	 * @param <T>
	 * @return
	 */
	<T> String formatMessageForDTO(T dto);
}
