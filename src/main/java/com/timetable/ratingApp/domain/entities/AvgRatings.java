package com.timetable.ratingApp.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvgRatings {
    private String toUserId;
    private double avgRating = 0.0;
    private int totalReviews = 0; // Total amount of reviews
}
