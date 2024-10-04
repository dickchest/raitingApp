package com.timetable.ratingApp.domain.entities;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDetails {
    private String id;
    private String email;
    private String password;
    private String displayName;

}
