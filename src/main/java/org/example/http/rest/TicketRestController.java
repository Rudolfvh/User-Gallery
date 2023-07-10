package org.example.http.rest;

import lombok.RequiredArgsConstructor;
import org.example.service.ImageService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.notFound;

@RestController
@RequestMapping("/api/v1/images")
@RequiredArgsConstructor
public class TicketRestController {

    private final ImageService imageService;

    @GetMapping(value = "/{id}")
    public ResponseEntity<byte[]> findImageById(@PathVariable("id") Long id) {
        return imageService.findImageByte(id)
                .map(content -> ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE,
                                MediaType.APPLICATION_OCTET_STREAM_VALUE)
                        .contentLength(content.length)
                        .body(content))
                .orElseGet(notFound()::build);
    }

}
