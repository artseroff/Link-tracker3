package edu.java.scrapper.client.github;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.scrapper.dto.github.RepositoryEventResponse;
import java.time.OffsetDateTime;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.util.UriComponentsBuilder;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

class GithubClientTest {
    private WireMockServer wireMockServer;
    private GithubClient client;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        client = new SimpleGithubClient(wireMockServer.baseUrl());
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void fetchLastModified_OkResponse() {

        // Arrange
        String owner = "torvalds";
        String repo = "linux";

        String responseBody = """
            [
              {
                "id": "35975145533",
                "type": "WatchEvent",
                "actor": {
                  "id": 113846655,
                  "login": "muzipp",
                  "display_login": "muzipp",
                  "gravatar_id": "",
                  "url": "https://api.github.com/users/muzipp",
                  "avatar_url": "https://avatars.githubusercontent.com/u/113846655?"
                },
                "repo": {
                  "id": 2325298,
                  "name": "torvalds/linux",
                  "url": "https://api.github.com/repos/torvalds/linux"
                },
                "payload": {
                  "action": "started"
                },
                "public": true,
                "created_at": "2024-02-24T20:05:19Z"
              }
            ]
            """;

        long expectedEventId = 35975145533L;
        String expectedType = "WatchEvent";
        String expectedActorName = "muzipp";
        OffsetDateTime expectedCreatedTime = OffsetDateTime.parse("2024-02-24T20:05:19Z");
        String expectedRepoName = "%s/%s".formatted(owner, repo);
        RepositoryEventResponse expectedResponse = new RepositoryEventResponse(
            expectedEventId,
            expectedType,
            expectedCreatedTime,
            new RepositoryEventResponse.Actor(expectedActorName),
            new RepositoryEventResponse.Repo(expectedRepoName)
        );

        String uriString = UriComponentsBuilder.newInstance()
            .path("/repos/{owner}/{repo}/events")
            .uriVariables(Map.of("owner", owner, "repo", repo))
            .queryParam("per_page", 1)
            .build()
            .toUriString();

        stubFor(get(urlEqualTo(uriString))
            .willReturn(aResponse().withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        // Arrange
        RepositoryEventResponse actualResponse = client.fetchLastModified("torvalds", "linux");

        // Assert
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void fetchLastModified_ErrorResponse() {

        // Arrange
        String owner = "_";
        String repo = "_";

        String responseBody = """
            {
               "message": "Not Found",
               "documentation_url": "https://docs.github.com/rest/repos/repos#get-a-repository"
             }
            """;

        String uriString = UriComponentsBuilder.newInstance()
            .path("/repos/{owner}/{repo}/events")
            .uriVariables(Map.of("owner", owner, "repo", repo))
            .queryParam("per_page", 1)
            .build()
            .toUriString();

        stubFor(get(urlEqualTo(uriString))
            .willReturn(aResponse().withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        // Arrange
        RepositoryEventResponse actualResponse = client.fetchLastModified("torvalds", "linux");

        // Assert
        Assertions.assertNull(actualResponse);
    }

}
