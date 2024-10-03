package com.timetable.ratingApp.services;

import com.google.firebase.auth.*;
import com.timetable.ratingApp.domain.entities.UserDetails;
import com.timetable.ratingApp.validation.NotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

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

    // admin
    public String setAdminRole(String uid, Principal principal, Boolean adminFlag) throws FirebaseAuthException {
        if (!isAdmin(principal)) return "User without admin rights can't change user's roles";
        // Set Custom Claims
        Map<String, Object> claims = new HashMap<>();
        claims.put("admin", adminFlag);

        // Set Custom claims for user
        try {
            FirebaseAuth.getInstance().setCustomUserClaims(uid, claims);
        } catch (FirebaseAuthException e) {
            throw new NotFoundException("User with UID " + uid + " not found!");
        }

        return "Custom claims set for user with UID: " + uid;
    }

    public boolean isAdmin(Principal principal) throws FirebaseAuthException {
        // get current auth from security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // check if Principal has object Jwt
        if (authentication.getPrincipal() instanceof Jwt jwt) {

            // Get token and check it with Firebase
            String idToken = jwt.getTokenValue();
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

            // check if admin word exist
            Boolean isAdmin = (Boolean) decodedToken.getClaims().get("admin");

            return isAdmin != null && isAdmin;
        }
        return false;
    }
}
