package com.martin.tube.payload;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.martin.tube.model.User;
import com.martin.tube.model.Video;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoPayload {

    @JsonUnwrapped
    private Video video;

    private Integer likeCount;
    private Integer dislikeCount;

    private Boolean liked = false;
    private Boolean disliked = false;

    private Integer commentCount;

    private ChannelPayload channel;

    public VideoPayload(Video video, User currentUser){
        this.video = video;
        this.likeCount = video.getUserLiked().size();
        this.dislikeCount = video.getUserDisliked().size();

        this.liked = video.getUserLiked().contains(currentUser);
        if (!liked)
            this.disliked = video.getUserDisliked().contains(currentUser);

        this.commentCount = video.getComments().size();

        this.channel = new ChannelPayload(video.getUploadedBy(), currentUser);
    }
}
