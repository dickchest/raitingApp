package com.timetable.ratingApp.domain.entities;

import lombok.Getter;
import lombok.Setter;

// Класс служит для того, что бы передавать значение uid в абстрактный класс репозитория
@Setter
@Getter
public abstract class BaseEntity {
    protected String uid;
}
