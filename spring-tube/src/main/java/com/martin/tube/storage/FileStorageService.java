package com.martin.tube.storage;

import com.google.common.hash.Hashing;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpRange;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class FileStorageService implements StorageService{

    public abstract Path getLocation();


    @Override
    @PostConstruct
    public void init() {
        try {
            if (!Files.exists(getLocation()))
                Files.createDirectory(getLocation());
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

            while (Files.exists(getLocation().resolve(filename + "." + ext))){
                filename = hash(filename);
            }

            file.transferTo(getLocation().resolve(filename + "." + ext));
        } catch (IOException e){
            throw new StorageException("Failed to store file", e);
        }

        return filename + "." + ext;
    }

    public ResourceRegion resourceRegion(String fileName, HttpRange range) throws IOException {
        Path videoPath = getLocation().resolve(fileName);
        UrlResource videoUrlResource = new UrlResource(videoPath.toUri());

        return resourceRegion(videoUrlResource, range);
    }

    private ResourceRegion resourceRegion(UrlResource resource, HttpRange range) throws IOException {
        long contentLength = resource.contentLength();

        if (range == null){
            long rangeLength = Math.min(1024 * 1024, contentLength);
            return new ResourceRegion(resource, 0, rangeLength);
        } else {
            long rangeStart = range.getRangeStart(contentLength),
                    rangeEnd = range.getRangeEnd(contentLength);
            long rangeLength = Math.min(1024 * 1024, rangeEnd - rangeStart + 1);
            return new ResourceRegion(resource, rangeStart, rangeLength);
        }
    }

    @Override
    public Path load(String name) {
        return getLocation().resolve(name);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            return new UrlResource(load(filename).toUri());
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public void delete(String fileName) throws IOException {
        Path path = load(fileName);
        Files.delete(path);
    }

    String hash(String s){
        return Hashing.adler32().hashString(s, StandardCharsets.UTF_8).toString();
    }

}
