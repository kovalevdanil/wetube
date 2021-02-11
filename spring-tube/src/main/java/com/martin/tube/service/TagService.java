package com.martin.tube.service;

import com.martin.tube.model.Tag;
import com.martin.tube.model.TagStats;
import com.martin.tube.model.User;
import com.martin.tube.model.id.TagStatsId;
import com.martin.tube.repository.TagRepository;
import com.martin.tube.repository.TagStatsRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final TagStatsRepository tagStatsRepository;

    public TagService(TagRepository tagRepository, TagStatsRepository tagStatsRepository) {
        this.tagRepository = tagRepository;
        this.tagStatsRepository = tagStatsRepository;
    }

    public Iterable<Tag> findAllByIds(Iterable<Long> ids){
        return tagRepository.findAllById(ids);
    }

    public Optional<Tag> findById(Long id){
        return tagRepository.findById(id);
    }

    public Optional<Tag> findByName(String name){
        return tagRepository.findByName(name);
    }

    public void updateStats(User user, Collection<Tag> tags){

        if (user == null || tags == null)
            return;

        tags.forEach((tag) -> {
            TagStatsId id = new TagStatsId(user.getId(), tag.getId());
            TagStats tagStats = tagStatsRepository.findById(id).orElse(null);

            if (tagStats == null){
                tagStats = new TagStats(id, 1);
            } else {
                tagStats.setCount(tagStats.getCount() + 1);
            }

            tagStatsRepository.save(tagStats);
        });
    }

}
