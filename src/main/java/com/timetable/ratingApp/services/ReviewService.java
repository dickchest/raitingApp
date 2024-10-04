package com.timetable.ratingApp.services;

import com.timetable.ratingApp.domain.entities.Reviews;
import com.timetable.ratingApp.repository.ReviewRepositoryImpl;
import com.timetable.ratingApp.validation.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
public class ReviewService {
    private final FirebaseAuthService firebaseAuthService;
    private final AvgRatingService avgRatingService;
    private final ReviewRepositoryImpl repository;

    public List<Reviews> getAll() throws ExecutionException, InterruptedException {
        return repository.getAll();
    }

    public String create(Reviews entity, Principal principal) throws ExecutionException, InterruptedException {

        entity.setFromUserId(firebaseAuthService.getUserUid(principal));

        // updating average rating
        avgRatingService.updateAvgRating(entity.getToUserId(), entity.getRating(), 0);

        return repository.save(entity);
    }

    public Reviews get(String documentId) {
        return repository.findById(documentId)
                .orElseThrow(() -> new NotFoundException("Entity Not found!"));
    }

    public String update(Reviews entity, Principal principal) throws ExecutionException, InterruptedException {
        // check if account exists
        Reviews request = get(entity.getUid());

        // check if it's user's own review
        isCurrentUser(request, principal);

        // updating average rating
        avgRatingService.updateAvgRating(request.getToUserId(), entity.getRating(), request.getRating());

        // updates fields
        Optional.ofNullable(entity.getRating()).ifPresent(request::setRating);
        Optional.ofNullable(entity.getComment()).ifPresent(request::setComment);

        return repository.save(request);
    }

    public String delete(String uid, Principal principal){
        // check if document exists
        Reviews request = get(uid);

        // check if it's user's own review
//        isCurrentUser(request, principal);

        // updating average rating
        avgRatingService.updateAvgRating(request.getToUserId(), 0, request.getRating());

        return repository.delete(uid);
    }

    private void isCurrentUser(Reviews request, Principal principal) {
        if (!request.getFromUserId().equals(firebaseAuthService.getUserUid(principal))) {
            throw new RuntimeException("Not allowed!");
        }
    }
}
