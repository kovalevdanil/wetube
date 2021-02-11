package com.martin.tube.payload;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import com.martin.tube.model.Comment;
import com.martin.tube.model.User;
import com.martin.tube.model.mapping.UserMapping;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class CommentTree {

    private Long id;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    private Date date;
    private Boolean edited;

    @JsonView(UserMapping.Basic.class)
    private User user;

    @JsonAlias("children_total")
    private Integer childrenTotal;

    private Integer likes;
    private Integer dislikes;

    private List<CommentTree> children;

    public CommentTree(Comment comment, Integer depth){
        BeanUtils.copyProperties(comment, this);

        likes = comment.getUsersLiked().size();
        dislikes = comment.getUsersDisliked().size();
        childrenTotal = comment.getChildren().size();

        if (depth <= -1 || depth > 1) {
            children = comment.getChildren().stream()
                    .map(c -> new CommentTree(c, depth - 1))
                    .collect(Collectors.toList());
        }
    }

}
