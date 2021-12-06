package com.unic.sapcc.toolkit.dto;

public record BuildRequestDTO(
		String applicationCode,
		String branch,
		String name) {
}
