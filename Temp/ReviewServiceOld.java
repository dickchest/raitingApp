package com.timetable.ratingApp.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.timetable.ratingApp.domain.entities.Reviews;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class ReviewServiceOld {
    private final FirebaseAuthService firebaseAuthService;
    private final AvgRatingService avgRatingService;
    private final Firestore dbFirestore = FirestoreClient.getFirestore();
    private final CollectionReference collection = dbFirestore.collection("reviews");

    public ReviewServiceOld(FirebaseAuthService firebaseAuthService, AvgRatingService avgRatingService) {
        this.firebaseAuthService = firebaseAuthService;
        this.avgRatingService = avgRatingService;
    }

    public List<Reviews> getAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = collection.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream().map(x -> x.toObject(Reviews.class)).toList();
    }

    public String create(Reviews entity, Principal principal) throws ExecutionException, InterruptedException {
        DocumentReference addedDocRef = collection.document();

        entity.setUid(addedDocRef.getId());
        entity.setFromUserId(firebaseAuthService.getUserUid(principal));
        addedDocRef.set(entity);

        // updating average rating
        avgRatingService.updateAvgRating(entity.getToUserId(), entity.getRating(), 0);

        return addedDocRef.getId();
    }

    public Reviews get(String documentId) {
        DocumentSnapshot document = checkIfExistDocument(documentId);
        return document.toObject(Reviews.class);
    }

    public String update(Reviews entity, Principal principal) throws ExecutionException, InterruptedException {
        // check if account exists
        Reviews request = get(entity.getUid());
        System.out.println(entity.getToUserId());

        // check if it's user's own review
        isCurrentUser(request, principal);

        // updating average rating
        System.out.println("entity = " + entity.getToUserId() + ", new rating = " + entity.getRating() + ", old rating = " + request.getRating());
        avgRatingService.updateAvgRating(request.getToUserId(), entity.getRating(), request.getRating());

        // проверяем каждое поле
        Optional.ofNullable(entity.getRating()).ifPresent(request::setRating);
        Optional.ofNullable(entity.getComment()).ifPresent(request::setComment);

        ApiFuture<WriteResult> collectionsApiFuture = collection.document(entity.getUid()).set(request);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public String delete(String documentId, Principal principal) throws ExecutionException, InterruptedException {
        // check if document exists
        Reviews request = get(documentId);

        // check if it's user's own review
        isCurrentUser(request, principal);

        // updating average rating
        avgRatingService.updateAvgRating(request.getToUserId(), 0, request.getRating());

        collection.document(documentId).delete();

        return "Successfully deleted " + documentId;
    }

    private void isCurrentUser(Reviews request, Principal principal) {
        if (!request.getFromUserId().equals(firebaseAuthService.getUserUid(principal))) {
            throw new RuntimeException("Not allowed!");
        }
    }

    private DocumentSnapshot checkIfExistDocument(String documentId) {
        DocumentReference documentReference = collection.document(documentId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        try {
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                return document;
            } else {
                throw new RuntimeException("Entity Not Found");
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
