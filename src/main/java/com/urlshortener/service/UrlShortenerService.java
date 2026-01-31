package com.urlshortener.service;

import com.urlshortener.dto.UrlRequest;
import com.urlshortener.dto.UrlResponse;
import com.urlshortener.model.UrlMapping;
import com.urlshortener.repository.UrlMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {

    private final UrlMappingRepository repository;
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private final SecureRandom random = new SecureRandom();

    @Transactional
    public UrlResponse shortenUrl(UrlRequest request, String baseUrl) {
        // Check if URL already shortened
        Optional<UrlMapping> existing = repository.findByOriginalUrl(request.getLongUrl());
        if (existing.isPresent()) {
            return mapToResponse(existing.get(), baseUrl);
        }

        String shortCode = generateUniqueShortCode();
        UrlMapping mapping = UrlMapping.builder()
                .originalUrl(request.getLongUrl())
                .shortCode(shortCode)
                .clickCount(0)
                .build();

        UrlMapping saved = repository.save(mapping);
        return mapToResponse(saved, baseUrl);
    }

    @Transactional
    public String getOriginalUrl(String shortCode) {
        UrlMapping mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("Short URL not found"));
        
        mapping.setClickCount(mapping.getClickCount() + 1);
        repository.save(mapping);
        
        return mapping.getOriginalUrl();
    }

    private String generateUniqueShortCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (repository.findByShortCode(code).isPresent());
        return code;
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    private UrlResponse mapToResponse(UrlMapping mapping, String baseUrl) {
        return UrlResponse.builder()
                .originalUrl(mapping.getOriginalUrl())
                .shortUrl(baseUrl + "/" + mapping.getShortCode())
                .shortCode(mapping.getShortCode())
                .createdAt(mapping.getCreatedAt())
                .clickCount(mapping.getClickCount())
                .build();
    }
}
