package com.martin.tube.storage;

import com.google.common.hash.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class VideoStorageService implements StorageService{

    private final Path location;

    @Autowired
    public VideoStorageService(VideoStorageProperties props){
         location = Paths.get(props.getVideoLocation());
    }

    @Override
    @PostConstruct
    public void init() {
        try {
            if (!Files.exists(location))
                Files.createDirectory(location);
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage location", e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = filename.split("\\.")[1];
        filename = hash(filename);

        try {
            if (file.isEmpty()){
                throw new StorageException("Could not store empty file " + filename);
            }

            if (filename.contains("..")){
                throw new StorageException("Cannot store files with relative path outside current directory");
            }

            while (Files.exists(location.resolve(filename + "." + ext))){
                filename = hash(filename);
            }

            try (InputStream input = file.getInputStream()) {
                file.transferTo(this.location.resolve(filename + "." + ext));
//                Files.copy(input, this.location.resolve(filename + "." + ext));
            }
        } catch (IOException e){
            throw new StorageException("Failed to store file", e);
        }

        return filename + "." + ext;
    }

    public ResourceRegion resourceRegion(String videoName, HttpRange range) throws IOException {
        Path videoPath = location.resolve(videoName);
        UrlResource videoUrlResource = new UrlResource(videoPath.toUri());

        return resourceRegion(videoUrlResource, range);
    }

    private ResourceRegion resourceRegion(UrlResource video, HttpRange range) throws IOException {
        long contentLength = video.contentLength();

        if (range == null){
            long rangeLength = Math.min(1024 * 1024, contentLength);
            return new ResourceRegion(video, 0, rangeLength);
        } else {
            long rangeStart = range.getRangeStart(contentLength),
                    rangeEnd = range.getRangeEnd(contentLength);
            long rangeLength = Math.min(1024 * 1024, rangeEnd - rangeStart + 1);
            return new ResourceRegion(video, rangeStart, rangeLength);
        }
    }

    public Path getVideoPath(String filename){
        return this.location.resolve(filename);
    }

    @Override
    public Stream<Path> loadAll() {
        return null;
    }

    @Override
    public Path load(String name) {
        return null;
    }

    @Override
    public Resource loadAsResource(String filename) {
        return null;
    }

    @Override
    public void deleteAll() {

    }

    String hash(String s){
        return Hashing.adler32().hashString(s, StandardCharsets.UTF_8).toString();
    }


}
