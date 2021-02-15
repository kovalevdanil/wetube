package com.martin.tube;

import com.martin.tube.config.AppProperties;
import com.martin.tube.storage.StorageProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class, StorageProperties.class})
public class TubeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TubeApplication.class, args);
    }

}
