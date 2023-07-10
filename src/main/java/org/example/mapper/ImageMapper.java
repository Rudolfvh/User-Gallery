package org.example.mapper;

import org.example.database.entity.Image;
import org.example.dto.ImageReadDto;
import org.springframework.stereotype.Component;

@Component
public class ImageMapper {

    public ImageReadDto toReadDto(Image image) {
        return ImageReadDto.builder()
                .id(image.getId())
                .path(image.getPath())
                .build();
    }

}
