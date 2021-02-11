package com.martin.tube.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "videos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @Column(columnDefinition = "bigint not null default 0")
    private Long views = 0L;

    @Column(unique = true)
    private String slug;

    @Column(unique = true)
    private String uri;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
    @Column(name = "upload_date")
    private Date uploadDate;

    private Long duration; // duration in seconds

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User uploadedBy;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "user_video_like",
        joinColumns = {@JoinColumn(name = "video_id")},
        inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private Set<User> userLiked = new HashSet<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "user_video_dislike",
        joinColumns = {@JoinColumn(name = "video_id")},
        inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private Set<User> userDisliked = new HashSet<>();

    @OneToMany(mappedBy = "video")
    @JsonIgnore
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            joinColumns = @JoinColumn(name = "video_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @Override
    public String toString(){
        return title;
    }


    @Override
    public boolean equals(Object obj){
        if (obj == null)
            return false;

        if (obj == this)
            return true;

        if (obj.getClass() != this.getClass())
            return false;

        Video video = (Video) obj;

        return video.getId()!= null && video.getId().equals(this.id);
    }

    @Override
    public int hashCode(){
//        return id == null ? super.hashCode() : id.hashCode();
        return id == null ? 1 : id.hashCode();
    }
}
