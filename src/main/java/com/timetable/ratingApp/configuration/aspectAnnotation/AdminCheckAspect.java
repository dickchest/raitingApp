package com.timetable.ratingApp.configuration.aspectAnnotation;

import com.timetable.ratingApp.services.FirebaseAuthService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AdminCheckAspect {

    private final FirebaseAuthService firebaseAuthService;

    public AdminCheckAspect(FirebaseAuthService firebaseAuthService) {
        this.firebaseAuthService = firebaseAuthService;
    }

    @Before("@annotation(com.timetable.ratingApp.domain.annotation.IsAdmin)")
    public void checkIfAdmin() {
        System.out.println("Зашли в аспект isAdmin");
        System.out.println(firebaseAuthService.isAdmin());
        if (!firebaseAuthService.isAdmin()) {
            throw new RuntimeException("User in not admin");
        }
    }
}
