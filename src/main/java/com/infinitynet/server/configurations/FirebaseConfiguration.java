package com.infinitynet.server.configurations;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

import static com.infinitynet.server.Constants.FIREBASE_ADMIN_JSON_PATH;

@Configuration
@Slf4j
public class FirebaseConfiguration {

    private GoogleCredentials googleCredentials;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        getGoogleCredentials();
        FirebaseOptions firebaseOptions = FirebaseOptions
                .builder()
                .setCredentials(googleCredentials)
                .setStorageBucket("infinity-net-999.appspot.com")
                .build();

        if (FirebaseApp.getApps().isEmpty()) return FirebaseApp.initializeApp(firebaseOptions);
        return FirebaseApp.getInstance();
    }

    public void getGoogleCredentials() throws IOException {
        googleCredentials = GoogleCredentials.fromStream(new FileInputStream(FIREBASE_ADMIN_JSON_PATH));
    }

}
