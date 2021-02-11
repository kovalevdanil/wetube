package com.martin.tube.model.id;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
public class TagStatsId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "tag_id")
    private Long tagId;

    public TagStatsId(Long userId, Long tagId) {
        this.userId = userId;
        this.tagId = tagId;
    }

}
