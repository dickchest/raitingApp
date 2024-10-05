package com.timetable.ratingApp.controllers;

import com.google.firebase.auth.FirebaseAuthException;
import com.timetable.ratingApp.domain.annotation.IsAdmin;
import com.timetable.ratingApp.domain.entities.UserDetails;
import com.timetable.ratingApp.services.FirebaseAuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AppController {
    private FirebaseAuthService authService;

    @GetMapping("/getUid")
    public String getPrincipalName(Principal principal) {
        return authService.getUserUid(principal);
    }

    @GetMapping("/getEmail")
    public String getPrincipalEmail(Principal principal) {
        return authService.getUserEmail(principal);
    }

    @GetMapping("/getAllUid")
    public ResponseEntity<List<String>> getAll() {
        return new ResponseEntity<>(authService.getAllUid(), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody UserDetails user) throws FirebaseAuthException {
        return new ResponseEntity<>(authService.create(user), HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateUser(@RequestBody UserDetails user) throws FirebaseAuthException {
        return new ResponseEntity<>(authService.update(user), HttpStatus.ACCEPTED);
    }

    @GetMapping("/getUser")
    public ResponseEntity<UserDetails> getUser(@RequestParam String documentId) {
        return new ResponseEntity<>(authService.findByUid(documentId), HttpStatus.OK);
    }

    @IsAdmin
    @GetMapping("/setAdminTrue")
    public String setAdminTrue(@RequestParam String documentId) throws Exception {
        return authService.setAdminRole(documentId, true);
    }

    @IsAdmin
    @GetMapping("/setAdminFalse")
    public String setAdminFalse(@RequestParam String documentId) throws Exception {
        return authService.setAdminRole(documentId, false);
    }

    @GetMapping("/isAdmin")
    public Boolean isAdmin(Principal principal) throws Exception {
        return authService.isAdmin();
    }
}
