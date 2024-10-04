package com.timetable.ratingApp.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.timetable.ratingApp.domain.entities.Reviews;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.security.Principal;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class ReviewServiceTest {

    @InjectMocks
    private ReviewService reviewService;

    @Mock
    private FirebaseAuthService firebaseAuthServiceMock;

    @Mock
    private Firestore firestoreMock;

    @Mock
    private CollectionReference collectionMock;

    @Mock
    private DocumentReference documentReferenceMock;

    @Mock
    private ApiFuture<QuerySnapshot> querySnapshotFutureMock;

    @Mock
    private ApiFuture<DocumentSnapshot> documentSnapshotFutureMock;

    @Mock
    private ApiFuture<WriteResult> writeResultFutureMock;

    @Mock
    private QuerySnapshot querySnapshotMock;

    @Mock
    private QueryDocumentSnapshot queryDocumentSnapshotMock;

    @Mock
    private Principal principalMock;

//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        when(firestoreMock.collection("reviews")).thenReturn(collectionMock);
//        when(collectionMock.document(anyString())).thenReturn(documentReferenceMock);
//
//        reviewService = new ReviewService(firebaseAuthServiceMock, firestoreMock);
//    }

    @Test
    public void testCreateReview() throws ExecutionException, InterruptedException {
        // Arrange
        Reviews review = new Reviews();
        review.setComment("Test Comment");
        when(firebaseAuthServiceMock.getUserUid(principalMock)).thenReturn("user123");
        when(documentReferenceMock.getId()).thenReturn("reviewId123");
        when(documentReferenceMock.set(any(Reviews.class))).thenReturn(writeResultFutureMock);

        // Act
        String resultId = reviewService.create(review, principalMock);

        // Assert
        assertEquals("reviewId123", resultId);
        assertEquals("user123", review.getFromUserId());
        verify(documentReferenceMock, times(1)).set(any(Reviews.class));
    }

    @Test
    public void testGetAllReviews() throws ExecutionException, InterruptedException {
        // Arrange
        when(querySnapshotFutureMock.get()).thenReturn(querySnapshotMock);
        when(querySnapshotMock.getDocuments()).thenReturn(List.of(queryDocumentSnapshotMock));
        when(queryDocumentSnapshotMock.toObject(Reviews.class)).thenReturn(new Reviews());

        when(collectionMock.get()).thenReturn(querySnapshotFutureMock);

        // Act
        List<Reviews> reviews = reviewService.getAll();

        // Assert
        assertNotNull(reviews);
        assertEquals(1, reviews.size());
        verify(collectionMock, times(1)).get();
    }

    @Test
    public void testGetReviewById() throws ExecutionException, InterruptedException {
        // Arrange
        DocumentSnapshot documentSnapshotMock = mock(DocumentSnapshot.class);
        Reviews mockReview = new Reviews();
        when(documentSnapshotMock.exists()).thenReturn(true);
        when(documentSnapshotMock.toObject(Reviews.class)).thenReturn(mockReview);
        when(documentReferenceMock.get()).thenReturn(documentSnapshotFutureMock);
        when(documentSnapshotFutureMock.get()).thenReturn(documentSnapshotMock);

        // Act
        Reviews result = reviewService.get("reviewId123");

        // Assert
        assertNotNull(result);
        verify(documentReferenceMock, times(1)).get();
    }

    @Test
    public void testUpdateReview() throws ExecutionException, InterruptedException {
        // Arrange
        Reviews review = new Reviews();
        review.setUid("reviewId123");
        review.setFromUserId("user123");
        when(firebaseAuthServiceMock.getUserUid(principalMock)).thenReturn("user123");
        when(documentReferenceMock.get()).thenReturn(documentSnapshotFutureMock);
        when(documentSnapshotFutureMock.get().toObject(Reviews.class)).thenReturn(review);

        when(collectionMock.document(anyString()).set(any(Reviews.class))).thenReturn(writeResultFutureMock);

        // Act
        String result = reviewService.update(review, principalMock);

        // Assert
        assertNotNull(result);
        verify(collectionMock.document("reviewId123"), times(1)).set(review);
    }

    @Test
    public void testDeleteReview() throws ExecutionException, InterruptedException {
        // Arrange
        DocumentSnapshot documentSnapshotMock = mock(DocumentSnapshot.class);
        when(documentSnapshotMock.exists()).thenReturn(true);
        when(documentReferenceMock.get()).thenReturn(documentSnapshotFutureMock);
        when(documentSnapshotFutureMock.get()).thenReturn(documentSnapshotMock);
        when(collectionMock.document(anyString()).delete()).thenReturn(writeResultFutureMock);

        // Act
        String result = reviewService.delete("reviewId123", principalMock);

        // Assert
        assertEquals("Successfully deleted reviewId123", result);
        verify(collectionMock.document("reviewId123"), times(1)).delete();
    }
}
