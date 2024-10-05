package com.timetable.ratingApp.services;

import com.google.firebase.auth.FirebaseAuthException;
import com.timetable.ratingApp.domain.entities.UserDetails;
import com.timetable.ratingApp.repository.FirebaseAuthRepository;
import com.timetable.ratingApp.validation.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class FirebaseAuthService {
    private final FirebaseAuthRepository repository;

    public String getUserUid(Principal principal) {
        Optional<UserDetails> userDetails = repository.findById(principal.getName());

        return userDetails.map(UserDetails::getId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public String getUserEmail(Principal principal) {
        Optional<UserDetails> userDetails = repository.findById(principal.getName());

        return userDetails.map(UserDetails::getEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<UserDetails> getAll() {
        return repository.findAll();
    }

    public UserDetails findByUid(String uid) {
        return repository.findById(uid)
                .orElseThrow(() -> new NotFoundException("User not found!"));
    }

    public String create(UserDetails user) throws FirebaseAuthException {
        return repository.save(user);
    }

    public String update(UserDetails user) throws FirebaseAuthException {
        return repository.update(user);
    }

    public void delete(String uid) {
        repository.deleteById(uid);
    }

    // admin
    public String setAdminRole(String uid, Boolean adminFlag) {
        return repository.setAdminRole(uid, adminFlag);
    }

    public boolean isAdmin() {
        return repository.isAdmin();
    }
}
