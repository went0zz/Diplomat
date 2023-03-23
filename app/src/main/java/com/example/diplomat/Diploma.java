package com.example.diplomat;

import androidx.room.Entity;

import java.util.List;

@Entity(tableName = "diplomas")
public class Diploma {
    public int token;
    private final int schoolid;
    public int schoolId;
    String name;
    String surname;
    private final String patronymic;
    String pantonymic;
    String place;
    public Diploma(int token, int schoolid, String name, String surname, String patronymic, String place) {

        this.token = token;
        this.schoolid = schoolid;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.place = place;
    }
}
