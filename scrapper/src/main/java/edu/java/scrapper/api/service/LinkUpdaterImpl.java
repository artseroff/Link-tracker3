package edu.java.scrapper.api.service;

import edu.java.scrapper.api.repository.LinkRepository;

public class LinkUpdaterImpl implements LinkUpdater {
    private final LinkRepository linkRepository;

    public LinkUpdaterImpl(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    @Override
    public void update() {

    }
}
