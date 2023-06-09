package com.example.diplomat;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;

@Dao
public interface DiplomasDAO {
    @Query("SELECT COUNT(*) FROM diplomas")
    LiveData<Integer> count();
    @Query("SELECT * FROM diplomas")
    LiveData<List<Diploma>> getDiplomas();
    @Insert
    Completable add(Diploma diploma);
    @Query("SELECT * FROM diplomas WHERE token = :id")
    LiveData<Diploma> getDiploma(int id);
}
