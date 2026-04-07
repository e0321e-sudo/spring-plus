package org.example.expert.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.annotation.Auth;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.user.dto.request.UserChangePasswordRequest;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.service.S3Service;
import org.example.expert.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final S3Service s3Service;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUser(@PathVariable long userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    // 닉네임 정확히 일치해야 검색 가능
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getUsersByNickname(
            @RequestBody String nickname
    ) {
        return ResponseEntity.ok(userService.getUsersByNickname(nickname));
    }

    @PutMapping("/users")
    public void changePassword(@Auth AuthUser authUser, @RequestBody UserChangePasswordRequest userChangePasswordRequest) {
        userService.changePassword(authUser.getId(), userChangePasswordRequest);
    }

    @PatchMapping("/users/{userId}/profile-image")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable long userId,
            @RequestParam("file") MultipartFile file,
            @Auth AuthUser authUser
    ) throws IOException {
        String imageUrl = s3Service.uploadFile(file);
        userService.updateProfileImage(userId, imageUrl);
        return ResponseEntity.ok(imageUrl);
    }
}
