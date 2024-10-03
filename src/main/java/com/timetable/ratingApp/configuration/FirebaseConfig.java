package com.timetable.ratingApp.configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("classpath:serviceAccountKey.json")
    Resource resourceFile;

    @Bean
    public FirebaseAuth firebaseAuth() {
        return FirebaseAuth.getInstance();
    }


    @PostConstruct
    public void initializeFirebaseApp() throws IOException {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(resourceFile.getInputStream()))
                .setServiceAccountId("firebase-adminsdk-qtm0u@time-tracking-app-790ea.iam.gserviceaccount.com")
                .build();
        FirebaseApp.initializeApp(options);
    }
}
