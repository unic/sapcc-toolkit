package com.unic.sapcc.toolkit.conditions;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class TeamsWebhookCondition implements Condition {

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		return Boolean.parseBoolean(context.getEnvironment().getProperty("notification.teams.enabled"));
	}
}
