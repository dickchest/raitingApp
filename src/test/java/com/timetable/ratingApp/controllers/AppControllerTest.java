package com.timetable.ratingApp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.timetable.ratingApp.domain.entities.UserDetails;
import com.timetable.ratingApp.services.FirebaseAuthService;
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

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = AppController.class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@AutoConfigureMockMvc
class AppControllerTest {
    @Value("/auth")
    private String basePath;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FirebaseAuthService service;

    @InjectMocks
    private AppController controller;

    @Autowired
    private ObjectMapper mapper;

    private UserDetails testEntity;

    @BeforeEach
    void setUp() {
        testEntity = new UserDetails();
        testEntity.setId("1");
        testEntity.setEmail("test@email.com");
        testEntity.setPassword("test");
        testEntity.setDisplayName("test name");
    }

    @Test
    @WithMockUser
    void getPrincipalName() throws Exception {
        when(service.getUserUid(any())).thenReturn(testEntity.getDisplayName());

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + "/getUid"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(testEntity.getDisplayName()));
    }

    @Test
    @WithMockUser
    void getPrincipalEmail() throws Exception {
        when(service.getUserEmail(any())).thenReturn(testEntity.getEmail());

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + "/getEmail"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(testEntity.getEmail()));
    }

    @Test
    @WithMockUser
    void getAll() throws Exception {
        List<String> list = Arrays.asList("1", "2", "3");
        when(service.getAllUid()).thenReturn(list);

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + "/getAllUid"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$[0]").value("1"))
                .andExpect(jsonPath("$[1]").value("2"))
                .andExpect(jsonPath("$[2]").value("3"))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$", hasSize(3)))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void createUser() throws Exception {
        String expectedResponse = "123";

        when(service.create(any(UserDetails.class))).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(basePath + "/create")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testEntity)))
                .andExpect(status().isCreated())
                .andExpect(content().string(expectedResponse));

    }

    @Test
    @WithMockUser
    void updateUser() throws Exception {
        String expectedResponse = "123";
        when(service.update(any(UserDetails.class))).thenReturn(expectedResponse);

        mockMvc.perform(MockMvcRequestBuilders.put(basePath + "/update")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(testEntity)))
                .andExpect(status().isAccepted())
                .andExpect(content().string(expectedResponse));
    }

    @Test
    @WithMockUser
    void getUser() throws Exception {

        when(service.findByUid(testEntity.getId())).thenReturn(testEntity);

        mockMvc.perform(MockMvcRequestBuilders.get(basePath + "/getUser")
                        .param("documentId", "1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.displayName").value(testEntity.getDisplayName()))
                .andExpect(jsonPath("$.email").value(testEntity.getEmail()))
                .andExpect(jsonPath("$.id").value(testEntity.getId()));
    }
}