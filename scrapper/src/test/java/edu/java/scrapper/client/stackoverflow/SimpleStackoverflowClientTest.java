package edu.java.scrapper.client.stackoverflow;

import com.github.tomakehurst.wiremock.WireMockServer;
import edu.java.scrapper.dto.stackoverflow.QuestionAnswerResponse;
import java.time.Instant;
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

public class SimpleStackoverflowClientTest {

    private WireMockServer wireMockServer;
    private StackoverflowClient client;

    @BeforeEach
    void setUp() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        client = new SimpleStackoverflowClient(wireMockServer.baseUrl());
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void fetchLastModified_OkResponse() {

        // Arrange
        long questionId = 214741;

        String responseBody = """
            {
              "items": [
                {
                  "owner": {
                    "account_id": 18780810,
                    "reputation": 108,
                    "user_id": 13695519,
                    "user_type": "registered",
                    "profile_image": "https://i.stack.imgur.com/qkB7V.png?s=256&g=1",
                    "display_name": "MrIo",
                    "link": "https://stackoverflow.com/users/13695519/mrio"
                  },
                  "is_accepted": false,
                  "score": 3,
                  "last_activity_date": 1679923748,
                  "last_edit_date": 1679923748,
                  "creation_date": 1594293340,
                  "answer_id": 62813612,
                  "question_id": 214741,
                  "content_license": "CC BY-SA 4.0"
                }
              ],
              "has_more": true,
              "quota_max": 300,
              "quota_remaining": 264
            }
            """;

        String uriString = UriComponentsBuilder.newInstance()
            .path("/questions/%d/answers".formatted(questionId))
            .queryParam("pagesize", 1)
            .queryParam("site", "stackoverflow")
            .queryParam("sort", "activity")
            .queryParam("order", "desc")
            .build()
            .toUriString();

        stubFor(get(urlEqualTo(uriString))
            .willReturn(aResponse().withStatus(200)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        long expectedAnswerId = 62813612;
        long unixTime = 1679923748;
        OffsetDateTime expectedLastActivityTime = OffsetDateTime.parse(Instant.ofEpochSecond(unixTime).toString());
        String expectedOwnerName = "MrIo";
        QuestionAnswerResponse expectedResponse = new QuestionAnswerResponse(
            questionId,
            expectedAnswerId,
            expectedLastActivityTime,
            new QuestionAnswerResponse.Owner(expectedOwnerName)
        );

        // Act
        QuestionAnswerResponse actualResponse = client.fetchLastModified(questionId);

        // Assert
        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    @Test
    public void fetchLastModified_ErrorResponse() {

        // Arrange
        long questionId = Long.MAX_VALUE;

        String responseBody = """
            {
                "error_id": 400,
                "error_message": "ids",
                "error_name": "bad_parameter"
              }
            """;

        String uriString = UriComponentsBuilder.newInstance()
            .path("/questions/{question_id}/answers")
            .uriVariables(Map.of("question_id", questionId))
            .queryParam("pagesize", 1)
            .queryParam("site", "stackoverflow")
            .queryParam("sort", "activity")
            .queryParam("order", "desc")
            .build()
            .toUriString();

        stubFor(get(urlEqualTo(uriString))
            .willReturn(aResponse().withStatus(400)
                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .withBody(responseBody)));

        // Act
        QuestionAnswerResponse actualResponse = client.fetchLastModified(questionId);

        // Assert
        Assertions.assertNull(actualResponse);
    }

}
