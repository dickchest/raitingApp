package com.timetable.ratingApp.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.timetable.ratingApp.domain.entities.AvgRatings;
import com.timetable.ratingApp.repository.AvgRatingRepositoryImpl;
import com.timetable.ratingApp.validation.NotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
@AllArgsConstructor
public class AvgRatingService {
    private final AvgRatingRepositoryImpl repository;

    public List<AvgRatings> getAll() throws ExecutionException, InterruptedException {
        return repository.getAll();
    }

    private void create(String toUserId) throws ExecutionException, InterruptedException {
        // create record and adjust toUserId as uid
        AvgRatings entity = new AvgRatings(toUserId, 0.0, 0);

        // wait for record to be created
        repository.save(entity);
    }

    private void save(AvgRatings entity) throws ExecutionException, InterruptedException {
        // check if documents exits
        AvgRatings request = get(entity.getToUserId());

        // проверяем каждое поле
        Optional.of(entity.getAvgRating()).ifPresent(request::setAvgRating);
        Optional.of(entity.getTotalReviews()).ifPresent(request::setTotalReviews);

        ApiFuture<WriteResult> collectionsApiFuture = collection.document(entity.getToUserId()).set(request);
        collectionsApiFuture.get();
    }

    public void updateAvgRating(String toUserId, int newRating, int oldRating) throws ExecutionException, InterruptedException {

        // check if document exist
        AvgRatings entity;
        try {
            entity = get(toUserId);
        } catch (NotFoundException e) {
            // if entity doesn't exist, create one
            create(toUserId);
            entity = new AvgRatings(toUserId, 0.0, 0);
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
        save(entity);
    }

    public AvgRatings get(String documentId) {
        DocumentSnapshot document = checkIfExistDocument(documentId);
        return document.toObject(AvgRatings.class);
    }

    public String delete(String documentId) {
        // нужно проверить, есть ли документ
        checkIfExistDocument(documentId);
        collection.document(documentId).delete();
        return "Successfully deleted " + documentId;
    }

    private DocumentSnapshot checkIfExistDocument(String documentId) {
        DocumentReference documentReference = collection.document(documentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        try {
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                return document;
            } else {
                throw new NotFoundException("Entity in AvgRatings Not Found");
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
