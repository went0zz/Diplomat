package com.example.diplomat;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "diplomas")
public class Diploma {
    @PrimaryKey(autoGenerate = true)
    public int token;
    public String schoolName;
    public String name;
    public String surname;
    public String patronymic;
    public String place;
    public String olympiadName;
    public String subject;
    public Diploma(int token, String name, String surname, String patronymic, String schoolName, String place, String olympiadName, String subject) {
        this.token = token;
        this.schoolName = schoolName;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.place = place;
        this.olympiadName = olympiadName;
        this.subject = subject;
    }
    @Ignore
    public Diploma(String name, String surname, String patronymic, String schoolName, String place, String olympiadName, String subject) {
        this(0, name, surname, patronymic, schoolName, place, olympiadName, subject);
    }
}
