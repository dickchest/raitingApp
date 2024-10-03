package com.timetable.ratingApp.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.timetable.ratingApp.domain.OperationEnum;
import com.timetable.ratingApp.domain.entities.Reviews;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class ReviewService {
    private final FirebaseAuthService firebaseAuthService;
    private final AvgRatingService avgRatingService;
    private final Firestore dbFirestore = FirestoreClient.getFirestore();
    private final CollectionReference collection = dbFirestore.collection("reviews");

    public ReviewService(FirebaseAuthService firebaseAuthService, AvgRatingService avgRatingService) {
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

        entity.setId(addedDocRef.getId());
        entity.setFromUserId(firebaseAuthService.getUserUid(principal));
        ApiFuture<WriteResult> writeResult = addedDocRef.set(entity);

        // добавпть пересчет среднего рейтинга
        avgRatingService.addAvgRating(entity.getToUserId(), entity.getRating(), entity, OperationEnum.ADD);

        return addedDocRef.getId();
    }

    public Reviews get(String documentId) {
        DocumentSnapshot document = checkIfExistDocument(documentId);
        return document.toObject(Reviews.class);
    }

    public String update(Reviews entity, Principal principal) throws ExecutionException, InterruptedException {
        // проверка, есть ли документ
        Reviews request = get(entity.getId());

        // проверка, что редактируется свой отзыв
        if (!request.getFromUserId().equals(firebaseAuthService.getUserUid(principal))) {
            throw new RuntimeException("Not allowed!");
        }

        // проверяем каждое поле
        Optional.ofNullable(entity.getToUserId()).ifPresent(request::setToUserId);
        Optional.ofNullable(entity.getRating()).ifPresent(request::setRating);
        Optional.ofNullable(entity.getComment()).ifPresent(request::setComment);

        ApiFuture<WriteResult> collectionsApiFuture = collection.document(entity.getId()).set(request);
        return collectionsApiFuture.get().getUpdateTime().toString();
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
                throw new RuntimeException("Entity Not Found");
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
