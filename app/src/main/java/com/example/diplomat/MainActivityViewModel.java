package com.example.diplomat;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivityViewModel extends AndroidViewModel {
    private final DiplomasDatabase diplomasDatabase;
    private final CompositeDisposable compositeDisposable;
    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        diplomasDatabase = DiplomasDatabase.getInstance(application);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
    public LiveData<List<Diploma>> getDiplomas() {
        return diplomasDatabase.DiplomasDAO().getDiplomas();
    }
    public void add(List<Diploma> diplomas) {
        for (Diploma diploma: diplomas) {
            Disposable disposable = diplomasDatabase.DiplomasDAO().add(diploma).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
            compositeDisposable.add(disposable);
        }
    }
}
