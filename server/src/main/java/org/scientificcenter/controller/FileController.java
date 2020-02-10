package org.scientificcenter.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping(value = "/api/files")
public class FileController {

    @GetMapping
    public ResponseEntity<?> download(@RequestParam("filePath") final String filePath) {
        try {
            final Path path = Paths.get(filePath);
            final Resource resource = new UrlResource(path.toUri());
            if (resource.getFilename() == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8))
                    .body(resource);
        } catch (final MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}