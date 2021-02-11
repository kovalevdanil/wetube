package com.martin.tube.repository;

import com.martin.tube.model.Comment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface CommentRepository extends PagingAndSortingRepository<Comment, Long> {

     List<Comment> findAllByIdAndParentIsNull(Long videoId);

     @Query(value = "select * from comments where video_id = :videoId and parent_id is null", nativeQuery = true)
     List<Comment> findRootComments(Long videoId);

     @Query(value = "select * from comments where video_id = :videoId and parent_id is null offset :offset limit :limit",
             nativeQuery = true)
     List<Comment> findRootComments(Long videoId, Integer offset, Integer limit);
}
