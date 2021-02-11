package com.martin.tube.repository;

import com.martin.tube.model.Video;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface VideoRepository extends PagingAndSortingRepository<Video, Long> {
    Optional<Video> findBySlug(String slug);
    Boolean existsVideoBySlug(String slug);
}
