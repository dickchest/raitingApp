package com.timetable.ratingApp.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AvgRatings extends BaseEntity {
    private double avgRating = 0.0;
    private int totalReviews = 0; // Total amount of reviews

    public AvgRatings(String uid, double avgRating, int totalReviews) {
        super.uid = uid;
        this.avgRating = avgRating;
        this.totalReviews = totalReviews;
    }
}
