package com.timetable.ratingApp.repository;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import com.timetable.ratingApp.domain.entities.BaseEntity;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public abstract class AbstractRepositoryFirebase<T extends BaseEntity> {
    private final CollectionReference collection;
    private final Class<T> entityClass; // Поле для хранения класса сущности

    protected AbstractRepositoryFirebase(String collectionName, Class<T> entityClass) {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        this.collection = dbFirestore.collection(collectionName);
        this.entityClass = entityClass;
    }

    public List<T> getAll() throws ExecutionException, InterruptedException {
        ApiFuture<QuerySnapshot> future = collection.get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        return documents.stream()
                .map(x -> x.toObject(entityClass)).toList();
    }

    public String save(T entity) {
        DocumentReference addedDocRef;
        // генерируем юид
        if (entity.getUid() == null) {
            addedDocRef = collection.document();
            entity.setUid(addedDocRef.getId());
        } else {
            addedDocRef = collection.document(entity.getUid());
        }
        ApiFuture<WriteResult> writeResult = addedDocRef.set(entity);

        try {
            writeResult.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return entity.getUid();
    }

    public Optional<T> findById(String uid) {
        ApiFuture<DocumentSnapshot> future = collection.document(uid).get();
        DocumentSnapshot document;
        try {
            document = future.get();
            return Optional.ofNullable(document.toObject(entityClass));
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public String delete(String uid) {
        ApiFuture<WriteResult> collectionsApiFuture = collection.document(uid).delete();
        try {
            collectionsApiFuture.get();
            return "Successfully deleted " + uid;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<T> findByField(String fieldName, Object value) {
        ApiFuture<QuerySnapshot> future = collection.whereEqualTo(fieldName, value).get();
        try {
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            return Optional.of(documents.get(0).toObject(entityClass));
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
