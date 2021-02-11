package com.martin.tube.controller;

import com.martin.tube.exception.ResourceNotFoundException;
import com.martin.tube.model.Video;
import com.martin.tube.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@Controller
@RequestMapping(path = "/video")
@Slf4j
public class VideoStreamController {

    private final VideoService videoService;

    public VideoStreamController(VideoService videoService) {
        this.videoService = videoService;
    }


    @GetMapping("/{slug}")
    public ResponseEntity<ResourceRegion> streamVideo(@PathVariable String slug,
                                                      @RequestHeader HttpHeaders headers) throws IOException {

        Video video = videoService.findVideoBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Video", "slug", slug));

        log.info(slug + " requested. Range: " + headers.getRange().toString());

        ResourceRegion region = videoService.getVideoRegion(video, headers.getRange());

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(region.getResource()).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(region);
    }
}
