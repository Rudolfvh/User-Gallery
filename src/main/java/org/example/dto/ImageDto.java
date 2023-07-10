package org.example.dto;

import lombok.Value;
import org.springframework.web.multipart.MultipartFile;

@Value
public class ImageDto {

    MultipartFile image;

}
