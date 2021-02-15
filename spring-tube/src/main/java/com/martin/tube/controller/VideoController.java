package com.martin.tube.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.martin.tube.common.Action;
import com.martin.tube.exception.BadRequestException;
import com.martin.tube.exception.ResourceNotFoundException;
import com.martin.tube.exception.UnauthorizedException;
import com.martin.tube.model.Tag;
import com.martin.tube.model.User;
import com.martin.tube.model.Video;
import com.martin.tube.payload.VideoPayload;
import com.martin.tube.repository.UserRepository;
import com.martin.tube.security.CurrentUser;
import com.martin.tube.security.UserPrincipal;
import com.martin.tube.service.CommentService;
import com.martin.tube.service.TagService;
import com.martin.tube.service.VideoService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/videos")
public class VideoController {

    private final VideoService videoService;
    private final TagService tagService;
    private final CommentService commentService;

    private final UserRepository userRepository;

    public VideoController(VideoService videoService, TagService tagService, CommentService commentService, UserRepository userRepository) {
        this.videoService = videoService;
        this.tagService = tagService;
        this.commentService = commentService;
        this.userRepository = userRepository;
    }

    @GetMapping("/{slug}")
    public ResponseEntity<?> getVideo(@PathVariable String slug, @CurrentUser UserPrincipal userPrincipal){
        Video video = videoService.findVideoBySlug(slug).orElseThrow(ResourceNotFoundException::new);
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(ResourceNotFoundException::new);

        videoService.incrementViewCountIfNeeded(video, user);

        VideoPayload payload = new VideoPayload(video, user);

        return ResponseEntity.ok(payload);
    }

    @GetMapping("/recs")
    public ResponseEntity<?> getRecommendations(@CurrentUser UserPrincipal userPrincipal,
                                                @RequestParam(value = "count", defaultValue = "10") Integer count){
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        List<Video> recs = videoService.getRecommendedVideos(user, count);

        return ResponseEntity.ok(recs);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postVideo(@RequestParam String title,
                                       @RequestParam String description,
                                       @RequestParam("file") MultipartFile videoFile,
                                       @CurrentUser UserPrincipal userPrincipal){ // TODO check file format (allow only mp4, mkv, ...)


        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        Video video = videoService
                .saveVideo(videoFile, title, description, user);

        return ResponseEntity.ok(video);
    }

    @DeleteMapping("/{slug}")
    public ResponseEntity<?> deleteVideo(@PathVariable String slug,
                                         @CurrentUser UserPrincipal userPrincipal){
        Video video = findVideoAndAuthorize(slug, userPrincipal);

        if (videoService.removeVideo(video))
            return ResponseEntity.noContent().build();

        throw new BadRequestException("unable to delete video with slug " + slug);
    }

    @GetMapping("/{slug}/tags")
    public ResponseEntity<?> getVideoTags(@PathVariable String slug){
        Video video = videoService.findVideoBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Video", "slug", slug));

        return ResponseEntity.ok(video.getTags());
    }

    @PostMapping("/{slug}/tags")
    public ResponseEntity<?> postVideoTags(@PathVariable String slug, @RequestBody Long[] tagIds, @CurrentUser UserPrincipal user){

        Video video = findVideoAndAuthorize(slug, user);

        List<Tag> tags = new ArrayList<>();
        tagService.findAllByIds(Arrays.asList(tagIds)).forEach(tags::add);
        videoService.addTags(video, tags);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{slug}/tags")
    public ResponseEntity<?> deleteVideoTags(@PathVariable String slug, @RequestBody Long[] tagIds, @CurrentUser UserPrincipal user){

        Video video = findVideoAndAuthorize(slug, user);

        List<Tag> tags = new ArrayList<>();
        tagService.findAllByIds(Arrays.asList(tagIds)).forEach(tags::add);
        videoService.removeTags(video, tags);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("/{slug}/like")
    public ResponseEntity<?> likeVideo(@PathVariable String slug,
                                       @CurrentUser UserPrincipal userPrincipal,
                                       @RequestParam("action") Action action){

        Video video = videoService.findVideoBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Video", "slug", slug));

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        switch (action){
            case set:
                videoService.setLike(video, user);
                break;
            case unset:
                videoService.unsetDislike(video, user);
                break;
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{slug}/dislike")
    public ResponseEntity<?> dislikeVideo(@PathVariable String slug,
                                       @CurrentUser UserPrincipal userPrincipal,
                                       @Param("action") Action action){

        Video video = videoService.findVideoBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Video", "slug", slug));

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

        switch (action){
            case set:
                videoService.setDislike(video, user);
                break;
            case unset:
                videoService.unsetDislike(video, user);
                break;
        }

        return ResponseEntity.noContent().build();
    }


    private Video findVideoAndAuthorize(String slug, UserPrincipal userPrincipal){
        Video video = videoService.findVideoBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Video", "slug", slug));

        if (!userPrincipal.getId().equals(video.getUploadedBy().getId())) {
            throw new UnauthorizedException();
        }

        return video;
    }


}
