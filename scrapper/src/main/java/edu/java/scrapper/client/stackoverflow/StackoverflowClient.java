package edu.java.scrapper.client.stackoverflow;

import edu.java.scrapper.dto.stackoverflow.QuestionAnswerResponse;

public interface StackoverflowClient {
    QuestionAnswerResponse fetchLastModified(long questionId);
}
