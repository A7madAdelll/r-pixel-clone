package com.r.pixel.dto;

import com.r.pixel.entity.Pixel;
import java.time.LocalDateTime;

public record PixelDto(int x, int y, String color, String updatedBy, LocalDateTime updatedAt) {

	public static PixelDto from(Pixel pixel) {
		return new PixelDto(pixel.getX(), pixel.getY(), pixel.getColor(),
				pixel.getUpdatedBy(), pixel.getUpdatedAt());
	}
}
