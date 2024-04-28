package edu.java.scrapper.service.updater.sender;

import edu.java.request.LinkUpdateRequest;

public interface LinkUpdatesSender {
    void sendUpdates(LinkUpdateRequest updateRequest);
}
