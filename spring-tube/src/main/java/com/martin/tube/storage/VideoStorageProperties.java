package com.martin.tube.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage")
@Data
public class VideoStorageProperties {
    private String videoLocation; // location in resources directory
}
