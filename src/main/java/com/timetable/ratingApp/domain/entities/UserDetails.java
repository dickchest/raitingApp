package com.timetable.ratingApp.domain.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetails {
    private String id;
    private String email;
    private String password;
    private String displayName;
}
