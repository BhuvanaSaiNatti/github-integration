package com.redcarepharmacy.controller;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redcarepharmacy.model.Language;
import com.redcarepharmacy.model.Repository;
import com.redcarepharmacy.model.response.RepositoriesResponse;
import com.redcarepharmacy.service.GithubService;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = RepositoriesController.class)
public class RepositoriesControllerTest {

    @MockBean
    private GithubService githubService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testSuccess() throws Exception {

        ArgumentCaptor<Integer> limit = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> page = ArgumentCaptor.forClass(Integer.class);
        Repository repos = Repository.builder().id(1).build();
        Mockito.when(githubService.fetchReposSortedByStars(any(), any(), limit.capture(), page.capture()))
                .thenReturn(List.of(repos));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/repositories")
                .queryParam("language", Language.C.getName())
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        String response = result.getResponse().getContentAsString();
        RepositoriesResponse reposResult = objectMapper.readValue(response, RepositoriesResponse.class);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, reposResult.getItems().size());
        Assertions.assertEquals(30, limit.getValue());
        Assertions.assertEquals(1, page.getValue());
    }

    @Test
    public void testBadLanguage() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/repositories")
                .queryParam("language", "Invalid")
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String response = result.getResponse().getContentAsString();
        Assertions.assertEquals(400, result.getResponse().getStatus());
        Assertions.assertTrue(response.contains(
                Arrays.stream(Language.values()).collect(Collectors.toSet()).toString()));
        Mockito.verify(githubService, times(0)).fetchReposSortedByStars(any(), any(), anyInt(), anyInt());
    }

    @Test
    public void testBadLimit() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/repositories")
                .queryParam("limitCount", "200")
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Assertions.assertEquals(400, result.getResponse().getStatus());
        Mockito.verify(githubService, times(0)).fetchReposSortedByStars(any(), any(), anyInt(), anyInt());

        requestBuilder = MockMvcRequestBuilders.get("/repositories")
                .queryParam("limitCount", "0")
                .accept(MediaType.APPLICATION_JSON);

        result = mockMvc.perform(requestBuilder).andReturn();
        Assertions.assertEquals(400, result.getResponse().getStatus());
        Mockito.verify(githubService, times(0)).fetchReposSortedByStars(any(), any(), anyInt(), anyInt());
    }

    @Test
    public void testBadDate() throws Exception {

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/repositories")
                .queryParam("createdAfter", "2024-10-10T11:00:00")
                .accept(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Assertions.assertEquals(400, result.getResponse().getStatus());
        Mockito.verify(githubService, times(0)).fetchReposSortedByStars(any(), any(), anyInt(), anyInt());

        String response = result.getResponse().getContentAsString();
        Assertions.assertTrue(response.contains("future"));

        requestBuilder = MockMvcRequestBuilders.get("/repositories")
                .queryParam("createdAfter", "2024-10-10T11")
                .accept(MediaType.APPLICATION_JSON);

        result = mockMvc.perform(requestBuilder).andReturn();
        Assertions.assertEquals(400, result.getResponse().getStatus());
        Mockito.verify(githubService, times(0)).fetchReposSortedByStars(any(), any(), anyInt(), anyInt());

        response = result.getResponse().getContentAsString();
        Assertions.assertTrue(response.contains("Invalid date format"));
    }
}
