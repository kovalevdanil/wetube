package com.martin.tube.model;

import com.martin.tube.model.id.TagStatsId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_tag_stat")
public class TagStats {

    @EmbeddedId
    private TagStatsId id;

    private Long count;

    @ManyToOne
    @MapsId("user_id")
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @MapsId("tag_id")
    @JoinColumn(name = "tag_id", referencedColumnName = "id")
    private Tag tag;

    public TagStats(TagStatsId id, long count) {
        this.id = id;
        this.count = count;
    }
}
