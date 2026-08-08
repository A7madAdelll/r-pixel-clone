package com.r.pixel.controller;

import com.r.pixel.dto.PixelDto;
import com.r.pixel.dto.PlacePixelRequest;
import com.r.pixel.entity.User;
import com.r.pixel.service.CanvasService;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PixelController {

	private final CanvasService canvasService;

	public PixelController(CanvasService canvasService) {
		this.canvasService = canvasService;
	}

	@GetMapping("/canvas")
	public List<String> getCanvas() {
		return canvasService.getCanvas();
	}

	@GetMapping("/pixels/{x}/{y}")
	public PixelDto getPixel(@PathVariable int x, @PathVariable int y) {
		return canvasService.getPixel(x, y);
	}

	@PostMapping("/pixels")
	public PixelDto placePixel(@RequestBody PlacePixelRequest request, @AuthenticationPrincipal User user) {
		return canvasService.placePixel(request, user);
	}
}
