package com.timetable.ratingApp.repository;

import com.timetable.ratingApp.domain.entities.Reviews;
import org.springframework.stereotype.Repository;

@Repository
public class ReviewRepositoryImpl extends AbstractRepositoryFirebase<Reviews> {

    public ReviewRepositoryImpl() {
        super("reviews", Reviews.class);
    }
}
