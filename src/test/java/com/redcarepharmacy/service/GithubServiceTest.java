package com.redcarepharmacy.service;

import com.redcarepharmacy.model.Language;
import com.redcarepharmacy.model.Repository;
import com.redcarepharmacy.model.response.RepositoriesResponse;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
public class GithubServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    GithubService githubService;

    @Test
    public void testFetchReposSortedByStars() {
        ArgumentCaptor<URI> uriCapture = ArgumentCaptor.forClass(URI.class);
        ResponseEntity response = Mockito.mock(ResponseEntity.class);
        RepositoriesResponse repositoriesResponse = Mockito.mock(RepositoriesResponse.class);
        Mockito.when(response.getBody()).thenReturn(repositoriesResponse);
        Mockito.when(repositoriesResponse.getItems())
                .thenReturn(List.of(
                        Repository.builder().id(1).build(),
                        Repository.builder().id(2).build()));

        Mockito.when(restTemplate.exchange(
                        uriCapture.capture(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        String currentDate = LocalDate.now().toString();
        List<Repository> repositoryList =
                githubService.fetchReposSortedByStars(Language.JAVA.getName(), currentDate, 1, 1);
        Assertions.assertEquals(2, repositoryList.size());
        URI value = uriCapture.getValue();
        Assertions.assertTrue(value.getQuery().contains("language:Java"));
        Assertions.assertTrue(value.getQuery().contains("created:>=" + currentDate));
    }

    @Test
    public void testFetchReposSortedByStarsEncodedLanguage() {
        ArgumentCaptor<URI> httpEntityArgumentCaptor = ArgumentCaptor.forClass(URI.class);
        ResponseEntity response = Mockito.mock(ResponseEntity.class);
        RepositoriesResponse repositoriesResponse = Mockito.mock(RepositoriesResponse.class);
        Mockito.when(response.getBody()).thenReturn(repositoriesResponse);
        Mockito.when(repositoriesResponse.getItems())
                .thenReturn(List.of(Repository.builder().id(1).build()));

        Mockito.when(restTemplate.exchange(
                        httpEntityArgumentCaptor.capture(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        List<Repository> repositoryList = githubService.fetchReposSortedByStars("CSHARP", null, 1, 1);
        Assertions.assertEquals(1, repositoryList.size());
        Assertions.assertTrue(httpEntityArgumentCaptor.getValue().getQuery().contains("C#"));
    }

    @Test
    public void testFetchReposSortedByStarsDefault() {
        ArgumentCaptor<URI> uriCapture = ArgumentCaptor.forClass(URI.class);
        ResponseEntity response = Mockito.mock(ResponseEntity.class);
        RepositoriesResponse repositoriesResponse = Mockito.mock(RepositoriesResponse.class);
        Mockito.when(response.getBody()).thenReturn(repositoriesResponse);
        Mockito.when(repositoriesResponse.getItems())
                .thenReturn(List.of(Repository.builder().id(1).build()));

        Mockito.when(restTemplate.exchange(
                        uriCapture.capture(),
                        Mockito.any(),
                        Mockito.any(),
                        Mockito.any(ParameterizedTypeReference.class)))
                .thenReturn(response);

        List<Repository> repositoryList = githubService.fetchReposSortedByStars(null, null, 1, 1);
        Assertions.assertEquals(1, repositoryList.size());
        URI value = uriCapture.getValue();
        Assertions.assertTrue(value.getQuery().contains("q=Q"));
        Assertions.assertTrue(value.getQuery().contains("sort=stars&order=desc&per_page=1&page=1"));
    }
}
