package com.martin.tube.controller;

import com.martin.tube.exception.BadRequestException;
import com.martin.tube.exception.ResourceNotFoundException;
import com.martin.tube.model.User;
import com.martin.tube.payload.ChannelPayload;
import com.martin.tube.repository.UserRepository;
import com.martin.tube.security.CurrentUser;
import com.martin.tube.security.UserPrincipal;
import com.martin.tube.storage.AvatarStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final AvatarStorageService avatarStorageService;

    public UserController(UserRepository userRepository, AvatarStorageService avatarStorageService) {
        this.userRepository = userRepository;
        this.avatarStorageService = avatarStorageService;
    }

    @GetMapping("/me")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<?> getMe(@CurrentUser UserPrincipal userPrincipal){
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable Long id){
        User user = findUser(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/c")
    public ResponseEntity<?> getChannel(@PathVariable Long id, @CurrentUser UserPrincipal userPrincipal){
        User user = findUser(id);

        Boolean subscribed = user.getSubscribers().stream()
                .anyMatch(u -> u.getId().equals(userPrincipal.getId()));

        ChannelPayload payload = new ChannelPayload(user, subscribed, user.getSubscribers().size());

        return ResponseEntity.ok(payload);
    }

    @PostMapping("/{id}/subscribe")
    public ResponseEntity<?> subscribeToUser(@PathVariable Long id, @CurrentUser UserPrincipal userPrincipal){
        User currentUser = findUser(userPrincipal.getId());
        User user = findUser(id);

        if (user.equals(currentUser)){
            throw new BadRequestException("You can't subscribe to yourself");
        }

        if (user.getSubscribers().add(currentUser))
            userRepository.save(user);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/unsubscribe")
    public ResponseEntity<?> unsubscribeUser(@PathVariable Long id, @CurrentUser UserPrincipal userPrincipal){

        User currentUser = findUser(userPrincipal.getId());
        User user = findUser(id);

        if (user.equals(currentUser)){
            throw new BadRequestException("You can't unsubscribe yourself");
        }

        if (user.getSubscribers().remove(currentUser)){
            userRepository.save(user);
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/subscriptions")
    public ResponseEntity<?> getSubscriptions(@PathVariable Long id,
                                              @RequestParam(value = "page", defaultValue = "0") Integer page,
                                              @RequestParam(value = "size", defaultValue = "10") Integer size){
        if (page < 0 || size <= 0) {
            throw new BadRequestException("Page shouldn't be less than zero. Size should be greater than 0");
        }

        User user = findUser(id);

        List<User> users = user.getSubscriptions()
                .stream().skip(page * size).limit(size).collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}/subscribers")
    public ResponseEntity<?> getSubscribers(@PathVariable Long id,
                                            @RequestParam(value = "page", defaultValue = "0") Integer page,
                                            @RequestParam(value = "size", defaultValue = "10") Integer size){

        if (page < 0 || size <= 0) {
            throw new BadRequestException("Page shouldn't be less than zero. Size should be greater than 0");
        }

        User user = findUser(id);

        List<User> users = user.getSubscribers()
                .stream().skip(page * size).limit(size).collect(Collectors.toList());

        return ResponseEntity.ok(users);

    }

    @PostMapping("/me/avatar")
    public ResponseEntity<?> postAvatar(@RequestParam MultipartFile avatar,
                                        @CurrentUser UserPrincipal userPrincipal){
        User user = findUser(userPrincipal.getId());

        String filename = avatarStorageService.store(avatar);

        user.setImageUrl(avatarStorageService.getUrl(filename));
        userRepository.save(user);

        return ResponseEntity.noContent().build();
    }

    private User findUser(long id){
        return userRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }
}
