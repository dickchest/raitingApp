package com.timetable.ratingApp.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.firebase.auth.*;
import com.timetable.ratingApp.domain.entities.Reviews;
import com.timetable.ratingApp.domain.entities.UserDetails;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class FirebaseAuthService {

    public String getUserUid(Principal principal) {
        try {
            UserRecord userRecord = FirebaseAuth.getInstance().getUser(principal.getName());
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }
        return principal.getName();
    }

    public String getUserEmail(Principal principal) {
        UserRecord userRecord = null;
        try {
            userRecord = FirebaseAuth.getInstance().getUser(principal.getName());
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }
        return userRecord.getEmail();
    }

    public List<String> getAll() {
        List<ExportedUserRecord> allUsers = getAllUserRecords();
        return allUsers.stream().map(UserRecord::getUid).toList();
    }


    private List<ExportedUserRecord> getAllUserRecords() {
        List<ExportedUserRecord> users = new ArrayList<>();
        ListUsersPage page;

        try {
            page = FirebaseAuth.getInstance().listUsers(null);
            page.iterateAll().forEach(users::add);
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }

        return users;
    }

    public String create(UserDetails user) throws FirebaseAuthException {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .setDisplayName(user.getDisplayName());

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        return userRecord.getUid();
    }

    public String update(UserDetails user) throws FirebaseAuthException {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(user.getId());
        Optional.ofNullable(user.getEmail()).ifPresent(request::setEmail);
        Optional.ofNullable(user.getPassword()).ifPresent(request::setPassword);
        Optional.ofNullable(user.getDisplayName()).ifPresent(request::setDisplayName);

        UserRecord userRecord = FirebaseAuth.getInstance().updateUser(request);
        return userRecord.getUid();
    }

    public UserInfo[] getUser(String documentId) {
        try {
            return FirebaseAuth.getInstance().getUser(documentId).getProviderData();
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("User not found");
        }
    }
}
