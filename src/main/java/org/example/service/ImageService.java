package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.example.database.entity.Image;
import org.example.database.entity.User;
import org.example.database.repository.ImageRepository;
import org.example.database.repository.UserRepository;
import org.example.dto.ImageReadDto;
import org.example.mapper.ImageMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;

    @Value("${app.image.bucket}")
    private String bucket; //где храниться картинка

    @Transactional
    public void uploadImage(Long userId, MultipartFile image) { //сохраняет картинку в бд и связывает ее с юзером
        if (!image.isEmpty()) {
            userRepository.findById(userId)
                    .ifPresent(user -> {
                        try {
                            upload(user, image.getOriginalFilename(), image.getInputStream());
                        } catch (IOException exception) {
                            exception.printStackTrace();
                        }
                    });
        }
    }

    private void upload(User user, String imagePath, InputStream content) {
        Path fullImagePath = Path.of(bucket, imagePath);
        try (content) {
            Files.write(fullImagePath, content.readAllBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        imageRepository.save(Image.builder().path(imagePath).user(user).build());
    }

    @Transactional(readOnly = true)
    public Optional<byte[]> findImageByte(Long id) {
        return imageRepository.findById(id).map(image -> get(image.getPath()));
    }

    @Transactional(readOnly = true)
    public Optional<ImageReadDto> findById(Long id) {
        return imageRepository.findById(id).map(imageMapper::toReadDto);
    }

    @Transactional(readOnly = true)
    public List<ImageReadDto> findAllByUserId(Long userId) {
        return imageRepository.findAllByUserId(userId).stream()
                .map(imageMapper::toReadDto).collect(Collectors.toList());
    }

    @Transactional
    public void remove(Long id, String imagePath) {
        Path fullImagePath = Path.of(bucket, imagePath);
        try (var inputStream = new ByteArrayInputStream(findImageByte(id).get());) {
            Files.delete(fullImagePath);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        imageRepository.deleteById(id);
    }

    @Transactional
    public void update(Long id, MultipartFile fileImage) {
        if (!fileImage.isEmpty()) {
            imageRepository.findById(id).ifPresent(image -> {
                Path fullImagePath = Path.of(bucket, image.getPath());
                try (var content = fileImage.getInputStream()) {
                    Files.write(fullImagePath, content.readAllBytes(),
                            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            });
        }
    }

    @SneakyThrows
    private byte[] get(String imagePath) {
        Path fullImagePath = Path.of(bucket, imagePath);
        return Files.readAllBytes(fullImagePath);
    }

}
