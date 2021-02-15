package com.martin.tube.service;

import com.github.slugify.Slugify;
import com.martin.tube.exception.BadRequestException;
import com.martin.tube.model.*;
import com.martin.tube.model.id.ViewId;
import com.martin.tube.repository.CommentRepository;
import com.martin.tube.repository.TagStatsRepository;
import com.martin.tube.repository.VideoRepository;
import com.martin.tube.repository.ViewRepository;
import com.martin.tube.storage.VideoStorageService;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpRange;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VideoService {

    private final static List<String> supportedContentType = Arrays.asList(
            "video/mp4",        //mp4
            "video/quicktime",  // .mov
            "video/x-ms-wmv",   // .wmv
            "video/x-msvideo"   // .avi
    );

    private final static int viewThresholdSeconds = 30;

    private final ViewRepository viewRepository;
    private final VideoRepository videoRepository;
    private final TagStatsRepository tagStatsRepository;
    private final VideoStorageService videoStorageService;
    private final CommentRepository commentRepository;


    public VideoService(ViewRepository viewRepository, VideoRepository videoRepository, TagStatsRepository tagStatsRepository, VideoStorageService videoStorageService, CommentRepository commentRepository) {
        this.viewRepository = viewRepository;
        this.videoRepository = videoRepository;
        this.tagStatsRepository = tagStatsRepository;
        this.videoStorageService = videoStorageService;
        this.commentRepository = commentRepository;
    }

    public Optional<Video> findVideoBySlug(String slug){
        return videoRepository.findBySlug(slug);
    }

    public Optional<Video> findVideoById(Long id){
        return videoRepository.findById(id);
    }

    public Video saveVideo(MultipartFile videoFile, String title, String description, User user){

        if (!isSupportedContentType(videoFile.getContentType())){
            throw new BadRequestException("Supported mime types: " +
                    supportedContentType.toString());
        }

        Video video = new Video();

        // save the video file, get its name in video root directory
        final String fileName = videoStorageService.store(videoFile);

        // find unique slug for the video title
        String slug = new Slugify().slugify(title),
                testSlug = slug;

        int slugCount = 0;
        while (videoRepository.existsVideoBySlug(testSlug)){
            testSlug = slug + slugCount;
            slugCount++;
        }
        slug = testSlug;

        // fill info about the video
        fillVideo(video, title, description, slug, fileName, user);

        // save video information
        video = videoRepository.save(video);

        return video;
    }

    public boolean removeVideo(Video video){

        String fileName = video.getUri();

        try {
            videoStorageService.delete(fileName);
            commentRepository.deleteAll(video.getComments());
            videoRepository.delete(video);
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public boolean incrementViewCountIfNeeded(@NotNull Video video, User user){
        if (user == null)
            return false;

        ViewId id = new ViewId(video.getId(), user.getId());

        View view = viewRepository.findById(id).orElse(null);

        if (view == null){
            view = new View(id, new Date());
            video.setViews(video.getViews() + 1);

            viewRepository.save(view);
            videoRepository.save(video);

            return true;
        } else  {
            Date oldLastTime = view.getLastTime();
            Date newLastTime = new Date();

            long oldLastTimeMs = oldLastTime.getTime(), // time in ms
                newLastTimeMs = newLastTime.getTime();

            // if viewThresholdSeconds passed since last view
            boolean incrementViewCount = ((newLastTimeMs - oldLastTimeMs) / 1000 > viewThresholdSeconds);

            if (incrementViewCount){
                video.setViews(video.getViews() + 1);
                videoRepository.save(video);
            }

            view.setLastTime(newLastTime);
            viewRepository.save(view);

            return incrementViewCount;
        }
    }

    public void setLike(Video video, User user){

        unsetDislike(video, user);

        if (video.getUserLiked().add(user))
            videoRepository.save(video);
   }

   public void setDislike(Video video, User user){
        unsetLike(video, user);

        if (video.getUserDisliked().add(user))
            videoRepository.save(video);
   }

   public void unsetLike(Video video, User user){
        if (video.getUserLiked().remove(user))
            videoRepository.save(video);
   }

   public void unsetDislike(Video video, User user){
        if (video.getUserDisliked().remove(user))
            videoRepository.save(video);
   }

    public ResourceRegion getVideoRegion(Video videoInfo, List<HttpRange> rangeList) throws IOException {
        return videoStorageService.resourceRegion(videoInfo.getUri(),
                rangeList.stream().findFirst().orElse(null));
    }

    public ResourceRegion getVideoRegion(String fileName, List<HttpRange> rangeList) throws IOException {
        return videoStorageService.resourceRegion(fileName,
                rangeList.stream().findFirst().orElse(null));
    }

    public Set<Tag> addTags(Video video, Collection<Tag> tags){
         if (video.getTags().addAll(tags))
             videoRepository.save(video);
         return video.getTags();
    }

    public Set<Tag> removeTags(Video video, Collection<Tag> tags){
        if (video.getTags().removeAll(tags))
            videoRepository.save(video);
        return video.getTags();
    }

    /*
    *  что нужно учитывать:
    *  - дату загрузки видео
    *  - количество просмотров
    *  - статистику
    * */
    public List<Video> getRecommendedVideos(User user, Integer number){
        List<TagStats> tagStats = tagStatsRepository.findAllByUser(user);

        List<Video> candidateVideos = new ArrayList<>(videoRepository
                .findAll(PageRequest.of(0, 100, Sort.by("uploadDate"))).toList());

        candidateVideos.sort((Video firstVideo, Video secondVideo) -> {
            long firstWeight = getWeight(firstVideo, tagStats);
            long secondWeight = getWeight(secondVideo, tagStats);

            return Long.compare(secondWeight, firstWeight);
        });

        return candidateVideos.stream().limit(number).collect(Collectors.toList());
    }

    public long getWeight(Video video, Collection<TagStats> stats){

        final int uploadDateWeight = 2;
        final int tagWeight = 3;
        final int viewsWeight = 3;

        // нужная функция с обратной зависимостью от количества секунд прошедших с момента загрузки

        long dateContrib =  video.getUploadDate().getTime() / (1000 * (new Date().getTime() - video.getUploadDate().getTime()));
        long viewsContrib = (video.getViews() / 100);

        Set<Tag> tags = video.getTags();

        long tagContrib = stats.stream()
                .filter(tagStat -> tags.contains(tagStat.getTag()))
                .map(TagStats::getCount)
                .reduce(0L, Long::sum);

        return uploadDateWeight * dateContrib + tagWeight * tagContrib + viewsWeight * viewsContrib;
    }

    private void fillVideo(Video video, String title, String description, String slug, String fileName, User user){
        video.setUploadDate(new Date());
        video.setTitle(title);
        video.setDescription(description);
        video.setUploadedBy(user);
        video.setSlug(slug);
        video.setUri(fileName);
        video.setUrl(videoStorageService.getUrl(fileName));
    }

    private boolean isSupportedContentType(String contentType){
        return supportedContentType.contains(contentType);
    }
}

