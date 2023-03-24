package com.example.diplomat;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Diploma.class}, version = 1)
public abstract class DiplomasDatabase extends RoomDatabase {
    private static final String DB_NAME = "diplomas.db";
    private static DiplomasDatabase instance;

    public static DiplomasDatabase getInstance(Application application) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    application,
                    DiplomasDatabase.class,
                    DB_NAME
            ).build();
        }

        return instance;
    }

    public abstract DiplomasDAO DiplomasDAO();
}
