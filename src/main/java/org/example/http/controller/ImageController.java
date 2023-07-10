package org.example.http.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.ImageDto;
import org.example.service.ImageService;
import org.example.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/gallery")
@RequiredArgsConstructor
public class ImageController {

    private final UserService userService;
    private final ImageService imageService;

    @GetMapping("/{userId}")
    public String userImages(@PathVariable Long userId, Model model) {
        userService.findById(userId)
                .ifPresent(user -> model.addAttribute("user", user));
        model.addAttribute("images", imageService.findAllByUserId(userId));
        return "user/gallery";
    }

    @PostMapping("/{userId}/add/image")
    public String addImage(@PathVariable Long userId, @ModelAttribute ImageDto imageDto) {
        imageService.uploadImage(userId, imageDto.getImage());
        return "redirect:/gallery/".concat(String.valueOf(userId));
    }

    @PostMapping("/{userId}/remove/image/{imageId}")
    public String remove(@PathVariable Long imageId,  @PathVariable Long userId) {
        imageService.findById(imageId).ifPresent(image ->imageService.remove(imageId, image.getPath()));
        return "redirect:/gallery/".concat(String.valueOf(userId));
    }

    @PostMapping("/{userId}/update/image/{imageId}")
    public String update(@PathVariable Long imageId,  @PathVariable Long userId, @ModelAttribute ImageDto imageDto) {
        imageService.update(imageId, imageDto.getImage());
        return "redirect:/gallery/".concat(String.valueOf(userId));
    }



}
