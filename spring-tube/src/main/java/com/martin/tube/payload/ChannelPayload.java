package com.martin.tube.payload;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.martin.tube.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChannelPayload {

    @JsonUnwrapped
    private User user;

    private Boolean subscribed;

    private Integer subscriberCount;

    public ChannelPayload(User channel, User currentUser){
        this.user = channel;
        this.subscribed = channel.getSubscribers().contains(currentUser);
        this.subscriberCount = channel.getSubscribers().size();
    }
}
