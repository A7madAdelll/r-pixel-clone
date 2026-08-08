package com.r.pixel.dto;

import java.time.LocalDateTime;

public record PixelUpdateMessage(int x, int y, String color, String updatedBy, LocalDateTime updatedAt) {
}
