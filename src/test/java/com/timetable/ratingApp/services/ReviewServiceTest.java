package com.timetable.ratingApp.services;

import com.timetable.ratingApp.domain.entities.Reviews;
import com.timetable.ratingApp.repository.ReviewRepositoryImpl;
import com.timetable.ratingApp.validation.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @Mock
    private FirebaseAuthService firebaseAuthService;
    @Mock
    private AvgRatingService avgRatingService;
    @Mock
    private ReviewRepositoryImpl repository;

    @InjectMocks
    private ReviewService reviewService;

    private Principal principal;
    private Reviews review;

    @BeforeEach
    void setUp() {
        principal = mock(Principal.class);
        review = new Reviews();
        review.setUid("reviewUid");
        review.setFromUserId("user1");
        review.setToUserId("user2");
        review.setRating(5);
        review.setComment("Great!");
    }

    @Test
    void getAll() throws ExecutionException, InterruptedException {
        // Arrange
        List<Reviews> reviewsList = new ArrayList<>();
        reviewsList.add(review);
        when(repository.getAll()).thenReturn(reviewsList);

        // Act
        List<Reviews> result = reviewService.getAll();

        // Assert
        assertEquals(1, result.size());
        assertEquals(review, result.get(0));
        verify(repository, times(1)).getAll();
    }

    @Test
    void create_ReviewSuccessfullyCreated() throws ExecutionException, InterruptedException {
        // Arrange
        when(firebaseAuthService.getUserUid(principal)).thenReturn("user1");
        when(repository.save(any(Reviews.class))).thenReturn("reviewUid");

        // Act
        String result = reviewService.create(review, principal);

        // Assert
        assertEquals("reviewUid", result);
        verify(avgRatingService, times(1)).updateAvgRating("user2", 5, 0);
        verify(repository, times(1)).save(review);

    }

    @Test
    void get_ReviewExists_ReturnsReview() {
        // Arrange
        when(repository.findById("reviewUid")).thenReturn(Optional.of(review));

        // Act
        Reviews result = reviewService.get("reviewUid");

        // Assert
        assertEquals(review, result);
        verify(repository, times(1)).findById("reviewUid");
    }

    @Test
    void get_ReviewDoesNotExist_ThrowsNotFoundException() {
        // Arrange
        when(repository.findById("reviewUid")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> reviewService.get("reviewUid"));
    }

    @Test
    void update_ReviewSuccessfullyUpdated() throws ExecutionException, InterruptedException {
        // Arrange
        Reviews updatedReview = new Reviews();
        updatedReview.setUid("reviewUid");
        updatedReview.setRating(4);
        updatedReview.setComment("Good");

        when(repository.findById("reviewUid")).thenReturn(Optional.of(review));
        when(firebaseAuthService.getUserUid(principal)).thenReturn("user1");
        when(repository.save(any(Reviews.class))).thenReturn("reviewUid");

        // Act
        String result = reviewService.update(updatedReview, principal);

        // Assert
        assertEquals("reviewUid", result);
        verify(avgRatingService, times(1)).updateAvgRating("user2", 4, 5);
        verify(repository, times(1)).save(any(Reviews.class));
    }

    @Test
    void delete_ReviewSuccessfullyDeleted() throws ExecutionException, InterruptedException {
        // Arrange
        when(repository.findById("reviewUid")).thenReturn(Optional.of(review));
        when(repository.delete("reviewUid")).thenReturn("Successfully deleted " + "reviewUid");

        // Act
        String result = reviewService.delete("reviewUid", principal);

        // Assert
        assertEquals("Successfully deleted " + "reviewUid", result);
        verify(avgRatingService, times(1)).updateAvgRating("user2", 0, 5);
        verify(repository, times(1)).delete("reviewUid");
    }

    @Test
    void isCurrentUser_ThrowsException_IfNotCurrentUser() throws NoSuchMethodException {
        // Arrange
        when(firebaseAuthService.getUserUid(principal)).thenReturn("otherUser");

        // Use reflection to access the private method
        Method isCurrentUserMethod = ReviewService.class.getDeclaredMethod("isCurrentUser", Reviews.class, Principal.class);
        isCurrentUserMethod.setAccessible(true);

        // Act & Assert
        assertThrows(InvocationTargetException.class, () -> {
            isCurrentUserMethod.invoke(reviewService, review, principal);
        });
    }
}