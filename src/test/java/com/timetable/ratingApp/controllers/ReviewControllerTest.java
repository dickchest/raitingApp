package com.timetable.ratingApp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timetable.ratingApp.domain.entities.Reviews;
import com.timetable.ratingApp.services.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.security.Principal;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = ReviewController.class)
class ReviewControllerTest {
    @Value("/reviews/")
    private String basePath;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReviewService service;

    @InjectMocks
    private ReviewController controller;

    @Autowired
    private ObjectMapper objectMapper;

    private Reviews testEntity;

    @BeforeEach
    void setUp() {
        testEntity = new Reviews("1", "fromUserId", "toUserId", 10, "testComment");
    }

    @Test
    @WithMockUser
    void getAll() throws Exception {
        List<Reviews> allEntities = Collections.singletonList(testEntity);

        when(service.getAll()).thenReturn(allEntities);

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + "getAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testEntity.getId()))
                .andExpect(jsonPath("$[0].fromUserId").value(testEntity.getFromUserId()))
                .andExpect(jsonPath("$[0].toUserId").value(testEntity.getToUserId()))
                .andExpect(jsonPath("$[0].rating").value(testEntity.getRating()))
                .andExpect(jsonPath("$[0].comment").value(testEntity.getComment()));

        verify(service).getAll();
    }

    @Test
    @WithMockUser
    void create() throws Exception {
        String createId = "abc123";

        when(service.create(any(Reviews.class), any(Principal.class))).thenReturn(createId);

        mockMvc.perform(MockMvcRequestBuilders.post(basePath + "create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEntity)))
                .andExpect(status().isCreated())
                .andExpect(content().string(createId));

        verify(service).create(any(Reviews.class), any(Principal.class));
    }

    @Test
    @WithMockUser
    void get() throws Exception {
        when(service.get(anyString())).thenReturn(testEntity);

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + "get")
                        .param("documentId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testEntity.getId()))
                .andExpect(jsonPath("$.fromUserId").value(testEntity.getFromUserId()))
                .andExpect(jsonPath("$.toUserId").value(testEntity.getToUserId()))
                .andExpect(jsonPath("$.rating").value(testEntity.getRating()))
                .andExpect(jsonPath("$.comment").value(testEntity.getComment()))
                .andDo(print());

        verify(service).get(anyString());
    }

    @Test
    @WithMockUser
    void update() throws Exception {
        String expectedResponse = "Expected Response";

        when(service.update(any(Reviews.class), any(Principal.class))).thenReturn(expectedResponse);

        mockMvc.perform(put(basePath + "update")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testEntity)))
                .andExpect(status().isAccepted())
                .andExpect(content().string(expectedResponse));

        verify(service).update(any(Reviews.class), any(Principal.class));
    }

    @Test
    @WithMockUser
    void delete() throws Exception {
        String expectedResponse = "Successfully deleted";

        when(service.delete(anyString())).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.delete(basePath + "delete")
                        .with(csrf())
                        .param("documentId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(content().string(expectedResponse));

        verify(service).delete(anyString());
    }
}