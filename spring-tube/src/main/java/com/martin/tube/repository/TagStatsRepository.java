package com.martin.tube.repository;

import com.martin.tube.model.TagStats;
import com.martin.tube.model.User;
import com.martin.tube.model.id.TagStatsId;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagStatsRepository extends CrudRepository<TagStats, TagStatsId> {

    List<TagStats> findAllByUser(User user);
}
