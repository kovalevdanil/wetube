package com.martin.tube.controller;

import com.martin.tube.storage.AvatarStorageService;
import com.martin.tube.storage.StorageProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/photo")
public class AvatarController {

    private final AvatarStorageService avatarStorageService;

    public AvatarController(AvatarStorageService avatarStorageService) {
        this.avatarStorageService = avatarStorageService;
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<ResourceRegion> getPhoto(@PathVariable String fileName,
                                                   @RequestHeader HttpHeaders headers) throws IOException {
        ResourceRegion region = avatarStorageService
                .resourceRegion(fileName, headers.getRange().stream().findFirst().orElse(null));

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(region.getResource()).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(region);
    }
}
