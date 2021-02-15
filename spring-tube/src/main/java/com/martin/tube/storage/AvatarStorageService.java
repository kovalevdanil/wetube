package com.martin.tube.storage;

import com.google.common.hash.Hashing;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.print.DocFlavor;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class AvatarStorageService extends FileStorageService{

    private Path location;
    private String baseUrl;

    public AvatarStorageService(StorageProperties props){
        location = Paths.get(props.getAvatarLocation());
        baseUrl = props.getPhotoUrlLocation();
    }

    @Override
    public Path getLocation(){
        return location;
    }

    public String getUrl(String fileName){
        return baseUrl + "/" + fileName;
    }

    public String getBaseUrl(){
        return baseUrl;
    }
}
