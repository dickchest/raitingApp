package com.timetable.ratingApp.services;

import com.timetable.ratingApp.domain.entities.AvgRatings;
import com.timetable.ratingApp.repository.AvgRatingRepositoryImpl;
import com.timetable.ratingApp.validation.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
public class AvgRatingService {
    private final AvgRatingRepositoryImpl repository;

    public List<AvgRatings> getAll() throws ExecutionException, InterruptedException {
        return repository.getAll();
    }

    private void create(String uid){
        // create record and adjust toUserId as uid
        AvgRatings entity = new AvgRatings(uid, 0.0, 0);

        // wait for record to be created
        repository.save(entity);
    }

    public void updateAvgRating(String uid, int newRating, int oldRating) throws ExecutionException, InterruptedException {

        // check if document exist
        AvgRatings entity;
        try {
            entity = get(uid);
        } catch (NotFoundException e) {
            // if entity doesn't exist, create one
            create(uid);
            entity = new AvgRatings(uid, 0.0, 0);
        }

        // rating counter in average formula
        int ratingCount = entity.getTotalReviews();

        if (newRating - oldRating == newRating) { // adding
            ratingCount++;
        } else if (newRating - oldRating == -oldRating) { // deleting
            ratingCount--;
        }
        // else counter remain the same, but not divide by 0
        double result = (ratingCount == 0)
                ? 0
                : (entity.getAvgRating() * entity.getTotalReviews() - oldRating + newRating) / ratingCount;

        entity.setAvgRating(result); // set new average rating
        entity.setTotalReviews(ratingCount);

        // safe updated record
        repository.save(entity);
    }

    public AvgRatings get(String uid) {
        return repository.findById(uid)
                .orElseThrow(() -> new NotFoundException("Not found!"));
    }

    public String delete(String uid) {
        // check if document exists
        get(uid);
        repository.delete(uid);
        return "Successfully deleted " + uid;
    }
}
