package com.timetable.ratingApp.domain.entities;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Reviews extends BaseEntity{

    @Setter
    private String fromUserId; // ID пользователя, который оставил отзыв.

    @Setter
    private String toUserId; // ID пользователя, которому оставлен отзыв.

    private Integer rating; // Рейтинг. May by only between 1 and 5

    @Setter
    private String comment; // Комментарий

    public void setRating(Integer rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        this.rating = rating;
    }
}
