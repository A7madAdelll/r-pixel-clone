package com.r.pixel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "pixels")
@IdClass(PixelId.class)
public class Pixel {

	@Id
	@Column(nullable = false)
	private int x;

	@Id
	@Column(nullable = false)
	private int y;

	@Column(nullable = false, length = 7)
	private String color;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "updated_by", length = 100)
	private String updatedBy;

	public Pixel(int x, int y) {
		this.x = x;
		this.y = y;
		this.color = "#FFFFFF";
	}
}
