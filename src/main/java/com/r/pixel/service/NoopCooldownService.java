package com.r.pixel.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NoopCooldownService implements CooldownService {

	private final int cooldownSeconds;

	public NoopCooldownService(@Value("${app.pixel.cooldown-seconds:0}") int cooldownSeconds) {
		this.cooldownSeconds = cooldownSeconds;
	}

	@Override
	public boolean isAllowed(String username) {
		return cooldownSeconds <= 0;
	}

	@Override
	public void recordPlacement(String username) {
	}
}
