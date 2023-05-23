package com.example.diplomat;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;

@Dao
public interface LoginDAO {
    @Query("SELECT * FROM accounts WHERE login = :login")
    LiveData<Account> getAccount(String login);
    @Insert
    Completable add(Account account);
    @Query("SELECT * FROM accounts WHERE entered = 1")
    LiveData<Account> isEntered();
    @Query("UPDATE accounts SET entered = 1 WHERE login = :login")
    Completable update(String login);
}
