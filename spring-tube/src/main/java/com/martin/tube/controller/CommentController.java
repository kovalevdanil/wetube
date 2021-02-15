package com.martin.tube.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.martin.tube.common.Action;
import com.martin.tube.exception.BadRequestException;
import com.martin.tube.exception.ResourceNotFoundException;
import com.martin.tube.exception.UnauthorizedException;
import com.martin.tube.model.Comment;
import com.martin.tube.model.User;
import com.martin.tube.model.Video;
import com.martin.tube.model.mapping.UserMapping;
import com.martin.tube.payload.CommentTree;
import com.martin.tube.payload.CommentUpload;
import com.martin.tube.repository.UserRepository;
import com.martin.tube.security.CurrentUser;
import com.martin.tube.security.UserPrincipal;
import com.martin.tube.service.CommentService;
import com.martin.tube.service.VideoService;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final VideoService videoService;

    private final UserRepository userRepository;

    public CommentController(CommentService commentService, VideoService videoService, UserRepository userRepository) {
        this.commentService = commentService;
        this.videoService = videoService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public ResponseEntity<?> getComments(@Param("slug") String slug, //
                                         @RequestParam(value = "depth", defaultValue = "-1") Integer depth,
                                         @RequestParam(value = "page", defaultValue = "0") Integer page,
                                         @RequestParam(value = "size", defaultValue = "10") Integer size){

        if (page < 0 || size < 0)
            throw new BadRequestException("Parameters page and size should be greater than 0");

        Video video = findVideo(slug);
        List<Comment> comments = commentService.getRootComments(video, page, size);

        return ResponseEntity.ok(comments.stream()
                .map(comment -> new CommentTree(comment, depth))
                .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getComment(@PathVariable Long id,
                                        @RequestParam(value = "depth", defaultValue = "-1") Integer depth){
        Comment comment = findComment(id);

        return ResponseEntity.ok(new CommentTree(comment, depth));
    }

    @PostMapping
    public ResponseEntity<?> postComment(@CurrentUser UserPrincipal userPrincipal,
                                         @Valid @RequestBody CommentUpload commentUpload,
                                         @RequestParam("slug") String slug){

        Video video = findVideo(slug);
        User user = findUser(userPrincipal.getId());

        Comment comment = commentService.saveComment(commentUpload.getContent(), video, user);

        return ResponseEntity.ok(new CommentTree(comment, 1));
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<?> postReply(@PathVariable Long id,
                                       @CurrentUser UserPrincipal userPrincipal,
                                       @Valid @RequestBody CommentUpload commentUpload){
        User user = findUser(userPrincipal.getId());

        Comment reply = commentService
                .replyTo(id, commentUpload.getContent(), user)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));

        return ResponseEntity.ok(reply);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id,
                                           @CurrentUser UserPrincipal userPrincipal){
        Comment comment = findComment(id);

        if (!comment.getUser().getId().equals(userPrincipal.getId())){
            throw new UnauthorizedException();
        }

        commentService.removeComment(comment);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeComment(@PathVariable Long id,
                                         @RequestParam Action action,
                                         @CurrentUser UserPrincipal userPrincipal){
        Comment comment = findComment(id);
        User user = findUser(userPrincipal.getId());

        performAction(action, comment, user,
                commentService::setLike,
                commentService::setDislike);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/dislike")
    public ResponseEntity<?> dislikeComment(@PathVariable Long id,
                                            @RequestParam Action action,
                                            @CurrentUser UserPrincipal userPrincipal){

        Comment comment = findComment(id);
        User user = findUser(userPrincipal.getId());

        performAction(action, comment, user,
                commentService::setDislike,
                commentService::unsetDislike);

        return ResponseEntity.noContent().build();
    }


    private void performAction(Action action, Comment comment, User user,
                               BiConsumer<Comment, User> setAction,
                               BiConsumer<Comment, User> unsetAction){
        switch(action){
            case set:
                setAction.accept(comment, user);
                break;
            case unset:
                unsetAction.accept(comment, user);
                break;
        }

    }

    private Video findVideo(String slug){
        return videoService.findVideoBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Video", "slug", slug));
    }

    private User findUser(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private Comment findComment(Long id){
        return commentService.findCommentById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
    }
}
