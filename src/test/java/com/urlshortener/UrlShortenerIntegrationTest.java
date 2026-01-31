package com.urlshortener;

import com.urlshortener.dto.UrlRequest;
import com.urlshortener.model.UrlMapping;
import com.urlshortener.repository.UrlMappingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class UrlShortenerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UrlMappingRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void shouldShortenUrl() throws Exception {
        UrlRequest request = new UrlRequest("https://resume-screener-eight.vercel.app/dashboard");

        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.originalUrl").value("https://resume-screener-eight.vercel.app/dashboard"))
                .andExpect(jsonPath("$.shortCode").exists())
                .andDo(print());
    }

    @Test
    void shouldRedirectToOriginalUrl() throws Exception {
        UrlMapping mapping = UrlMapping.builder()
                .originalUrl("https://www.github.com")
                .shortCode("git123")
                .clickCount(0)
                .build();
        repository.save(mapping);

        mockMvc.perform(get("/git123"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", "https://www.github.com"));
    }

    @Test
    void shouldReturn400ForInvalidUrl() throws Exception {
        UrlRequest request = new UrlRequest("invalid-url");

        mockMvc.perform(post("/api/shorten")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
