package com.infinitynet.server.configurations;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.BucketInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
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

//    @Bean
//    public Bucket firebaseStorageBucket() throws IOException {
//        if (googleCredentials == null) {
//            getGoogleCredentials();
//        }
//
//        // Get the Storage instance using the custom GoogleCredentials
//        Storage storage = StorageOptions.newBuilder()
//                .setCredentials(googleCredentials)
//                .build()
//                .getService();
//
//        // Get the bucket you want to use (it should already exist)
//        Bucket bucket = storage.get("infinity-net-999.appspot.com"); // Replace with your actual bucket name
//
//        log.info("Firebase Storage bucket: {}", bucket.getName());
//
//        return bucket;
//    }

    public void getGoogleCredentials() throws IOException {
        googleCredentials = GoogleCredentials.fromStream(new FileInputStream(FIREBASE_ADMIN_JSON_PATH));
    }

}
