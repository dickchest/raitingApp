package com.timetable.ratingApp.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.timetable.ratingApp.domain.entities.AvgRatings;
import com.timetable.ratingApp.validation.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class AvgRatingService {
    private final Firestore dbFirestore = FirestoreClient.getFirestore();
    private final CollectionReference collection = dbFirestore.collection("avg_ratings");

    public List<AvgRatings> getAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = collection.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream().map(x -> x.toObject(AvgRatings.class)).toList();
    }

    private String create(String toUserId) {
        DocumentReference addedDocRef = collection.document();
        AvgRatings entity = new AvgRatings(toUserId, 0.0, 0);

        ApiFuture<WriteResult> writeResult = addedDocRef.set(entity);

        return addedDocRef.getId();
    }

    private String save(AvgRatings entity) throws ExecutionException, InterruptedException {
        // check if documents exits
        AvgRatings request = get(entity.getToUserId());

        // проверяем каждое поле
        Optional.of(entity.getAvgRating()).ifPresent(request::setAvgRating);
        Optional.of(entity.getTotalReviews()).ifPresent(request::setTotalReviews);

        ApiFuture<WriteResult> collectionsApiFuture = collection.document(entity.getToUserId()).set(request);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public void updateAvgRating(String toUserId, int newRating, int oldRating) throws ExecutionException, InterruptedException {

        // check if document exist
        try {
            checkIfExistDocument(toUserId);
        } catch (NotFoundException e) {
            // if entity doesn't exist, create one
            create(toUserId);
        }
        // Get AvgRating by user Id
        AvgRatings entity = get(toUserId);

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

        // safe updated record
        save(entity);
    }


    public AvgRatings get(String documentId) {
        DocumentSnapshot document = checkIfExistDocument(documentId);
        return document.toObject(AvgRatings.class);
    }


    public String delete(String documentId) {
        // нужно проверить, есть ли документ
        DocumentSnapshot document = checkIfExistDocument(documentId);
        ApiFuture<WriteResult> collectionsApiFuture = collection.document(documentId).delete();
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
                throw new NotFoundException("Entity Not Found");
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
