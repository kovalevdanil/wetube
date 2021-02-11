package com.martin.tube.model;

import com.martin.tube.model.id.ViewId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "views")
public class View {

    @EmbeddedId
    private ViewId id;
    private Date lastTime;

    @ManyToOne
    @MapsId("video_id")
    @JoinColumn(name = "video_id", referencedColumnName = "id")
    private Video video;

    @ManyToOne
    @MapsId("user_id")
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;


    public View(ViewId id, Date date) {
        this.id = id;
        this.lastTime = date;
    }
}
