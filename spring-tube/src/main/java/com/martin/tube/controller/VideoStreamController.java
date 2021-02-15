package com.martin.tube.controller;

import com.martin.tube.exception.BadRequestException;
import com.martin.tube.exception.ResourceNotFoundException;
import com.martin.tube.model.Video;
import com.martin.tube.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping(path = "/video")
@Slf4j
public class VideoStreamController {

    private final VideoService videoService;

    public VideoStreamController(VideoService videoService) {
        this.videoService = videoService;
    }


    @GetMapping
    public ResponseEntity<ResourceRegion> streamVideo(@RequestParam("slug") String slug,
                                                      @RequestHeader HttpHeaders headers) throws IOException {

        Video video = videoService.findVideoBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Video", "slug", slug));

        log.info(slug + " requested. Range: " + headers.getRange().toString());

        ResourceRegion region = videoService.getVideoRegion(video, headers.getRange());

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(region.getResource()).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(region);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<ResourceRegion> streamVideoByName(@PathVariable String fileName,
                                                            @RequestHeader HttpHeaders headers){

        ResourceRegion region = null;
        try {
            region = videoService.getVideoRegion(fileName, headers.getRange());
        } catch (IOException ex){
            throw new BadRequestException("Unable to read file " + fileName);
        }

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(region.getResource()).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(region);
    }
}
