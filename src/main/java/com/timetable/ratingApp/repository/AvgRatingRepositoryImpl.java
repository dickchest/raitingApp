package com.timetable.ratingApp.repository;

import com.timetable.ratingApp.domain.entities.AvgRatings;
import org.springframework.stereotype.Repository;

@Repository
public class AvgRatingRepositoryImpl extends AbstractRepositoryFirebase<AvgRatings> {
    protected AvgRatingRepositoryImpl() {
        super("avg_ratings", AvgRatings.class);
    }
}
