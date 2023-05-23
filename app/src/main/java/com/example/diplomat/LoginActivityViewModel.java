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

public class LoginActivityViewModel extends AndroidViewModel {
    private final AccountsDatabase accountsDatabase;
    private final CompositeDisposable compositeDisposable;

    public LoginActivityViewModel(@NonNull Application application) {
        super(application);
        accountsDatabase = AccountsDatabase.getInstance(application);
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.dispose();
    }
    public void add(Account account) {
        Disposable disposable = accountsDatabase.loginDAO().add(account).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
        compositeDisposable.add(disposable);
    }
    public LiveData<Account> getAccount(String login) {
        return accountsDatabase.loginDAO().getAccount(login);
    }
    public LiveData<Account> isEntered() {
        return accountsDatabase.loginDAO().isEntered();
    }
    public void update(String login) {
        Disposable disposable = accountsDatabase.loginDAO().update(login).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
        compositeDisposable.add(disposable);
    }
}
