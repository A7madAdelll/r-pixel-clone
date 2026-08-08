package com.r.pixel.repository;

import com.r.pixel.entity.Pixel;
import com.r.pixel.entity.PixelId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PixelRepository extends JpaRepository<Pixel, PixelId> {

	Optional<Pixel> findByXAndY(int x, int y);
}
