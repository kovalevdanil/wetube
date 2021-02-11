package com.martin.tube.service;

import com.martin.tube.model.Comment;
import com.martin.tube.model.User;
import com.martin.tube.model.Video;
import com.martin.tube.repository.CommentRepository;
import com.martin.tube.repository.UserRepository;
import com.martin.tube.repository.VideoRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final VideoRepository videoRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository, VideoRepository videoRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
    }

    public Optional<Comment> findCommentById(Long id){
        return commentRepository.findById(id);
    }

    public Comment saveComment(String content, Video video, User user){

        Comment comment = new Comment();

        fillComment(comment, content, video, user);

        comment = commentRepository.save(comment);

        return comment;
    }

    public List<Comment> getRootComments(Video video){
        return commentRepository.findRootComments(video.getId());
    }

    public List<Comment> getRootComments(Video video, Integer page, Integer size){
        return commentRepository.findRootComments(video.getId(), page * size, size);
    }

    public void removeComment(Comment comment){

        List<Comment> children = comment.getChildren();
        if (children != null)
            comment.getChildren().forEach(this::removeComment);

        commentRepository.delete(comment);
    }

    public Optional<Comment> replyTo(Long commentId, String content, User user){
        Comment parentComment = commentRepository.findById(commentId).orElse(null);

        if (parentComment == null){
            return Optional.empty();
        }

        Comment comment = new Comment();
        fillComment(comment, content, parentComment.getVideo(), user);
        comment.setParent(parentComment);

        comment = commentRepository.save(comment);

        return Optional.ofNullable(comment);
    }

    public void setLike(Comment comment, User user){
        unsetDislike(comment, user);

        if (comment.getUsersLiked().add(user)){
            commentRepository.save(comment);
        }
    }
    public void setDislike(Comment comment, User user){
        unsetLike(comment, user);

        if (comment.getUsersDisliked().add(user)){
            commentRepository.save(comment);
        }

    }

    public void unsetLike(Comment comment, User user){
        if (comment.getUsersLiked().remove(user)){
            commentRepository.save(comment);
        }
    }

    public void unsetDislike(Comment comment, User user){
        if (comment.getUsersDisliked().remove(user)){
            commentRepository.save(comment);
        }
    }

    private void fillComment(Comment comment, String content, Video video, User user){
        comment.setContent(content);
        comment.setVideo(video);
        comment.setUser(user);
        comment.setDate(new Date());
        comment.setEdited(false);
    }
}
