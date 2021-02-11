package com.martin.tube.controller;

import com.martin.tube.exception.ResourceNotFoundException;
import com.martin.tube.model.User;
import com.martin.tube.repository.UserRepository;
import com.martin.tube.security.CurrentUser;
import com.martin.tube.security.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getMe(@CurrentUser UserPrincipal userPrincipal){
        User user = userRepository.findById(userPrincipal.getId()).orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(Long id){
        User user = userRepository.findById(id) .orElseThrow(ResourceNotFoundException::new);
        return ResponseEntity.ok(user);
    }
}
