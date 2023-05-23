package com.example.diplomat;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "accounts")
public class Account {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String login;
    public String password;
    public int entered;
    public Account(int id, String login, String password) {
        this.id = id;
        this.login = login;
        this.password = password;
        entered = 0;
    }
    @Ignore
    public Account(String login, String password) {
        this(0, login, password);
    }
}
