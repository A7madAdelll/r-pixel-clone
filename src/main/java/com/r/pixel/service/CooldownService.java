package com.r.pixel.service;

public interface CooldownService {

	boolean isAllowed(String username);

	void recordPlacement(String username);
}
