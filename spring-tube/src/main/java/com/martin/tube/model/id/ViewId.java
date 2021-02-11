package com.martin.tube.model.id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ViewId implements Serializable {

    @Column(name = "video_id")
    private Long videoId;
    @Column(name = "user_id")
    private Long userId;

    @Override
    public boolean equals(Object obj){
        if (obj == null)
            return false;

        if (obj == this)
            return true;

        if (obj.getClass() != this.getClass())
            return false;

        ViewId viewId = (ViewId) obj;

        return viewId.getUserId().equals(userId) && viewId.getVideoId().equals(videoId);
    }

    @Override
    public int hashCode(){
        int hash = 11;

        hash = 31 * hash + (videoId == null ? 0 : videoId.hashCode());
        hash = 31 * hash + (userId == null ? 0 : userId.hashCode());

        return hash;
    }

}
