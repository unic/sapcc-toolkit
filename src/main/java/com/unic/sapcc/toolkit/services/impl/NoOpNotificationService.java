package com.unic.sapcc.toolkit.services.impl;

import com.unic.sapcc.toolkit.services.NotificationService;

public class NoOpNotificationService implements NotificationService {
	@Override
	public <T> void sendMessage(T dto) {
		//NoOp
		return;
	}
}
