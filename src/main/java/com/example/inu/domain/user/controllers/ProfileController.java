package com.example.inu.domain.user.controllers;


import com.example.inu.domain.user.dtos.profiles.ProfileDto;
import com.example.inu.domain.user.dtos.profiles.ProfileUpdateDto;
import com.example.inu.domain.user.dtos.profiles.ProfileViewDto;
import com.example.inu.domain.user.entities.User;
import com.example.inu.domain.user.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RequestMapping("/api/v1/profiles")
@RestController
@RequiredArgsConstructor
public class ProfileController {

//    private  S3ImageService s3ImageService;


    private final ProfileService profileservice;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createProfile(@RequestPart("profile") ProfileDto profileDto,
                                           @RequestPart("photo") MultipartFile photo) {
        System.out.println(photo);
        System.out.println(profileDto);
        try {
            User user = profileservice.createProfile(profileDto, photo);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", user.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Profile creation failed"));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getProfile(@PathVariable Long userId){

        try{
            ProfileViewDto profile = profileservice.getProfile(userId);
            return ResponseEntity.ok(profile);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Profile not found: " +e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile() {
        try{
            ProfileViewDto profile = profileservice.getMyProfile();
            return ResponseEntity.ok(profile);
        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Profile not found: " +e.getMessage()));
        }
    }

    @PutMapping(value = "/{userId}", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateProfile(
            @PathVariable Long userId,
            @RequestPart("profile") ProfileUpdateDto profileUpdateDto,
            @RequestPart(value = "photo", required = false) MultipartFile photoFile) {
        try {
            profileservice.updateProfile(userId, profileUpdateDto, photoFile);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Failed to update profile: " + e.getMessage()));
        }
    }
    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteProfile(@PathVariable Long userId){
        try{
            profileservice.deleteProfile(userId);
            return ResponseEntity.ok().build();

        } catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to delete profile: "+e.getMessage()));
        }
    }
//    @PostMapping("/s3/upload")
//    public ResponseEntity<?> s3Upload(@RequestPart(value = "image", required = false) MultipartFile image){
//        String profileImage = s3ImageService.upload(image);
//        return ResponseEntity.ok(profileImage);
//    }

}