package dev.balusharma.urlshortener.application.service;

import dev.balusharma.urlshortener.application.context.CurrentUserContext;

public interface CurrentUserProvider {
    CurrentUserContext getCurrentUser();
}
