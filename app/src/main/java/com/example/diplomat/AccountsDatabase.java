package com.example.diplomat;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Account.class}, version = 1)
public abstract class AccountsDatabase extends RoomDatabase {
    private static final String DB_NAME = "accounts.db";
    private static AccountsDatabase instance;

    public static AccountsDatabase getInstance(Application application) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    application,
                    AccountsDatabase.class,
                    DB_NAME
            ).build();

        }

        return instance;
    }

    public abstract LoginDAO loginDAO();
}
