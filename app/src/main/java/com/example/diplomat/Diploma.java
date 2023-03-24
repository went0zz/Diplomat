package com.example.diplomat;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "diplomas")
public class Diploma {
    @PrimaryKey(autoGenerate = true)
    public int token;
    public int schoolid;
    public String name;
    public String surname;
    public String patronymic;
    public String pantonymic;
    public String place;

    public Diploma(int token, int schoolid, String name, String surname, String patronymic, String place) {
        this.token = token;
        this.schoolid = schoolid;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.place = place;
    }
    @Ignore
    public Diploma(int schoolid, String name, String surname, String patronymic, String place) {

        this.token = 0;
        this.schoolid = schoolid;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.place = place;
    }
}
