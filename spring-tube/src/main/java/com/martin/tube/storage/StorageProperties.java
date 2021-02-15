package com.martin.tube.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage")
@Data
public class StorageProperties {

    private String avatarLocation;
    private String videoLocation;
    private String photoUrlLocation;
    private String videoUrlLocation;
}
