package com.r.pixel.service;

import com.r.pixel.dto.PixelDto;
import com.r.pixel.dto.PixelUpdateMessage;
import com.r.pixel.dto.PlacePixelRequest;
import com.r.pixel.entity.Pixel;
import com.r.pixel.entity.PixelId;
import com.r.pixel.entity.User;
import com.r.pixel.exception.ApiException;
import com.r.pixel.repository.PixelRepository;
import com.r.pixel.websocket.PixelWebSocketHandler;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class CanvasService {

	public static final int SIZE = 100;
	private static final Pattern COLOR_PATTERN = Pattern.compile("^#[0-9A-Fa-f]{6}$");
	private static final String CANVAS_KEY = "canvas";

	private final PixelRepository pixelRepository;
	private final StringRedisTemplate redisTemplate;
	private final PixelWebSocketHandler webSocketHandler;
	private final CooldownService cooldownService;

	public CanvasService(PixelRepository pixelRepository, StringRedisTemplate redisTemplate,
			PixelWebSocketHandler webSocketHandler, CooldownService cooldownService) {
		this.pixelRepository = pixelRepository;
		this.redisTemplate = redisTemplate;
		this.webSocketHandler = webSocketHandler;
		this.cooldownService = cooldownService;
	}

	public List<String> getCanvas() {
		Map<Object, Object> cached = redisTemplate.opsForHash().entries(CANVAS_KEY);
		if (!cached.isEmpty()) {
			String[] colors = new String[SIZE * SIZE];
			cached.forEach((key, value) -> colors[parseIndex(key.toString())] = value.toString());
			return Arrays.asList(colors);
		}
		String[] colors = new String[SIZE * SIZE];
		Arrays.fill(colors, "#FFFFFF");
		Map<String, String> toCache = new HashMap<>(SIZE * SIZE);
		for (Pixel pixel : pixelRepository.findAll()) {
			if (pixel.getX() >= 0 && pixel.getX() < SIZE && pixel.getY() >= 0 && pixel.getY() < SIZE) {
				int index = pixel.getX() * SIZE + pixel.getY();
				colors[index] = pixel.getColor();
				toCache.put(key(pixel.getX(), pixel.getY()), pixel.getColor());
			}
		}
		redisTemplate.opsForHash().putAll(CANVAS_KEY, toCache);
		return Arrays.asList(colors);
	}

	public PixelDto placePixel(PlacePixelRequest request, User user) {
		validateBounds(request.x(), request.y());
		validateColor(request.color());
		if (!cooldownService.isAllowed(user.getUsername())) {
			throw new ApiException(HttpStatus.TOO_MANY_REQUESTS, "Wait before placing another pixel");
		}
		Pixel pixel = pixelRepository.findById(new PixelId(request.x(), request.y()))
				.orElseGet(() -> new Pixel(request.x(), request.y()));
		pixel.setColor(request.color().toUpperCase());
		pixel.setUpdatedAt(LocalDateTime.now());
		pixel.setUpdatedBy(user.getUsername());
		pixelRepository.save(pixel);
		cooldownService.recordPlacement(user.getUsername());
		redisTemplate.opsForHash().put(CANVAS_KEY, key(pixel.getX(), pixel.getY()), pixel.getColor());
		PixelDto dto = PixelDto.from(pixel);
		webSocketHandler.broadcast(new PixelUpdateMessage(dto.x(), dto.y(), dto.color(), dto.updatedBy(), dto.updatedAt()));
		return dto;
	}

	public PixelDto getPixel(int x, int y) {
		validateBounds(x, y);
		return pixelRepository.findById(new PixelId(x, y))
				.map(PixelDto::from)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Pixel not found"));
	}

	private static String key(int x, int y) {
		return x + ":" + y;
	}

	private static int parseIndex(String key) {
		int colon = key.indexOf(':');
		if (colon < 0) {
			return 0;
		}
		int x = Integer.parseInt(key.substring(0, colon));
		int y = Integer.parseInt(key.substring(colon + 1));
		return x * SIZE + y;
	}

	private static void validateBounds(int x, int y) {
		if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) {
			throw new ApiException(HttpStatus.BAD_REQUEST,
					"Coordinates must be between 0 and " + (SIZE - 1));
		}
	}

	private static void validateColor(String color) {
		if (color == null || !COLOR_PATTERN.matcher(color).matches()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Color must be a hex value like #FF0000");
		}
	}
}
