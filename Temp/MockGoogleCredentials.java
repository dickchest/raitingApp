package com.timetable.ratingApp.services;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import java.io.IOException;
import java.util.Date;

public class MockGoogleCredentials extends GoogleCredentials {
    private final AccessToken accessToken;

    public MockGoogleCredentials(String tokenValue, Date tokenExpiration) {
        this.accessToken = new AccessToken(tokenValue, tokenExpiration);
    }

    @Override
    public AccessToken refreshAccessToken() throws IOException {
        return accessToken;
    }
}
