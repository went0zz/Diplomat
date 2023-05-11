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
    public String place;

    public Diploma(int token, String name, String surname, String patronymic, int schoolid, String place) {
        this.token = token;
        this.schoolid = schoolid;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.place = place;
    }
    @Ignore
    public Diploma(String name, String surname, String patronymic, int schoolid, String place) {
        this(0, name, surname, patronymic, schoolid, place);
    }
}
