package edu.java.scrapper.client.github;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.scrapper.client.dto.github.CommitInfoResponse;
import edu.java.scrapper.client.dto.github.RepositoryEventResponse;
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

        // Act
        RepositoryEventResponse actualResponse = client.fetchLastModified(owner, repo);

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

        // Act
        RepositoryEventResponse actualResponse = client.fetchLastModified(owner, repo);

        // Assert
        Assertions.assertNull(actualResponse);
    }

    @Test
    public void fetchLastModifiedCommit_OkResponse() {

        // Arrange
        String owner = "torvalds";
        String repo = "linux";

        String responseBody = """
            [
               {
                 "sha": "906a93befec826475ab3d4facacc57a0b0a002a5",
                 "node_id": "C_kwDOACN7MtoAKDkwNmE5M2JlZmVjODI2NDc1YWIzZDRmYWNhY2M1N2EwYjBhMDAyYTU",
                 "commit": {
                   "author": {
                     "name": "Linus Torvalds",
                     "email": "torvalds@linux-foundation.org",
                     "date": "2024-03-17T19:26:04Z"
                   },
                   "committer": {
                     "name": "Linus Torvalds",
                     "email": "torvalds@linux-foundation.org",
                     "date": "2024-03-17T19:26:04Z"
                   },
                   "message": "Merge tag 'efi-fixes-for-v6.9-1'",
                   "tree": {
                     "sha": "d5b963f1c7a691f64dcd399832b27db1f207ff8f",
                     "url": "https://api.github.com/repos/torvalds/linux/git/trees/d5b963f1c7a691f64dcd399832b27db1f207ff8f"
                   },
                   "url": "https://api.github.com/repos/torvalds/linux/git/commits/906a93befec826475ab3d4facacc57a0b0a002a5",
                   "comment_count": 0,
                   "verification": {
                     "verified": false,
                     "reason": "unsigned",
                     "signature": null,
                     "payload": null
                   }
                 },
                 "url": "https://api.github.com/repos/torvalds/linux/commits/906a93befec826475ab3d4facacc57a0b0a002a5",
                 "html_url": "https://github.com/torvalds/linux/commit/906a93befec826475ab3d4facacc57a0b0a002a5",
                 "comments_url": "https://api.github.com/repos/torvalds/linux/commits/906a93befec826475ab3d4facacc57a0b0a002a5/comments",
                 "author": {
                   "login": "torvalds",
                   "id": 1024025,
                   "node_id": "MDQ6VXNlcjEwMjQwMjU=",
                   "avatar_url": "https://avatars.githubusercontent.com/u/1024025?v=4",
                   "gravatar_id": "",
                   "url": "https://api.github.com/users/torvalds",
                   "html_url": "https://github.com/torvalds",
                   "followers_url": "https://api.github.com/users/torvalds/followers",
                   "following_url": "https://api.github.com/users/torvalds/following{/other_user}",
                   "gists_url": "https://api.github.com/users/torvalds/gists{/gist_id}",
                   "starred_url": "https://api.github.com/users/torvalds/starred{/owner}{/repo}",
                   "subscriptions_url": "https://api.github.com/users/torvalds/subscriptions",
                   "organizations_url": "https://api.github.com/users/torvalds/orgs",
                   "repos_url": "https://api.github.com/users/torvalds/repos",
                   "events_url": "https://api.github.com/users/torvalds/events{/privacy}",
                   "received_events_url": "https://api.github.com/users/torvalds/received_events",
                   "type": "User",
                   "site_admin": false
                 },
                 "committer": {
                   "login": "torvalds",
                   "id": 1024025,
                   "node_id": "MDQ6VXNlcjEwMjQwMjU=",
                   "avatar_url": "https://avatars.githubusercontent.com/u/1024025?v=4",
                   "gravatar_id": "",
                   "url": "https://api.github.com/users/torvalds",
                   "html_url": "https://github.com/torvalds",
                   "followers_url": "https://api.github.com/users/torvalds/followers",
                   "following_url": "https://api.github.com/users/torvalds/following{/other_user}",
                   "gists_url": "https://api.github.com/users/torvalds/gists{/gist_id}",
                   "starred_url": "https://api.github.com/users/torvalds/starred{/owner}{/repo}",
                   "subscriptions_url": "https://api.github.com/users/torvalds/subscriptions",
                   "organizations_url": "https://api.github.com/users/torvalds/orgs",
                   "repos_url": "https://api.github.com/users/torvalds/repos",
                   "events_url": "https://api.github.com/users/torvalds/events{/privacy}",
                   "received_events_url": "https://api.github.com/users/torvalds/received_events",
                   "type": "User",
                   "site_admin": false
                 },
                 "parents": [
                   {
                     "sha": "8048ba24e1e678c595ceec76fed7c0c14f9cab1e",
                     "url": "https://api.github.com/repos/torvalds/linux/commits/8048ba24e1e678c595ceec76fed7c0c14f9cab1e",
                     "html_url": "https://github.com/torvalds/linux/commit/8048ba24e1e678c595ceec76fed7c0c14f9cab1e"
                   },
                   {
                     "sha": "b3810c5a2cc4a6665f7a65bed5393c75ce3f3aa2",
                     "url": "https://api.github.com/repos/torvalds/linux/commits/b3810c5a2cc4a6665f7a65bed5393c75ce3f3aa2",
                     "html_url": "https://github.com/torvalds/linux/commit/b3810c5a2cc4a6665f7a65bed5393c75ce3f3aa2"
                   }
                 ]
               }
             ]
            """;

        String expectedAuthorName = "torvalds";
        OffsetDateTime expectedCreatedTime = OffsetDateTime.parse("2024-03-17T19:26:04Z");

        CommitInfoResponse.Author author = new CommitInfoResponse.Author(expectedAuthorName);

        CommitInfoResponse.CommitNodeAuthor commitNodeAuthor =
            new CommitInfoResponse.CommitNodeAuthor(expectedCreatedTime);

        CommitInfoResponse.CommitNode commitNode =
            new CommitInfoResponse.CommitNode(commitNodeAuthor, "Merge tag 'efi-fixes-for-v6.9-1'");

        CommitInfoResponse expectedResponse = new CommitInfoResponse(author, commitNode);

        String uriString = UriComponentsBuilder.newInstance()
            .path("/repos/{owner}/{repo}/commits")
            .uriVariables(Map.of("owner", owner, "repo", repo))
            .queryParam("per_page", 1)
            .build()
            .toUriString();

        stubFor(get(urlEqualTo(uriString))
            .willReturn(aResponse().withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        // Act
        CommitInfoResponse actualResponse = client.fetchLastModifiedCommit(owner, repo);

        // Assert
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void fetchLastModifiedCommit_ErrorResponse() {

        // Arrange
        String owner = "_";
        String repo = "_";

        String responseBody = """
            {
                "message": "Not Found",
                "documentation_url": "https://docs.github.com/rest/commits/commits#list-commits"
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

        // Act
        CommitInfoResponse commitInfoResponse = client.fetchLastModifiedCommit(owner, repo);

        // Assert
        Assertions.assertNull(commitInfoResponse);
    }
}
