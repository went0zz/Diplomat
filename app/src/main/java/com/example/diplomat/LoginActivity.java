package com.example.diplomat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class LoginActivity extends AppCompatActivity {
    private EditText login, password;
    private TextView error;
    private LoginActivityViewModel loginActivityViewModel;
    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginActivityViewModel = new ViewModelProvider(this).get(LoginActivityViewModel.class);
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        button = findViewById(R.id.submit);
        error = findViewById(R.id.error);
        loginActivityViewModel.isEntered().observe(LoginActivity.this, result -> {
            if (result != null) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("olimpiad", result.login);
                startActivity(intent);
            }
            else {
                loginActivityViewModel.add(new Account("olimp1", "olimp1"));
                loginActivityViewModel.add(new Account("olimp2", "olimp2"));
                loginActivityViewModel.add(new Account("olimp3", "olimp3"));
            }
        });
        button.setOnClickListener(view -> {
            loginActivityViewModel.getAccount(login.getText().toString()).observe(LoginActivity.this, result -> {
                if (result == null || !Objects.equals(result.password, password.getText().toString())) {
                    error.setText("Неверное имя пользователя или пароль");
                }
                else {
                    loginActivityViewModel.update(login.getText().toString());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
        });
    }
}