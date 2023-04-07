package com.example.diplomat;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int READ_EXCEL_FILE = 1001;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1002;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1003;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 1004;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1005;

    private FloatingActionButton floatingActionButton, floatingCameraActionButton;
    private RecyclerView recyclerView;
    private static MainActivityViewModel mainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        floatingActionButton = findViewById(R.id.floating);
        recyclerView = findViewById(R.id.notesRecycler);
        floatingCameraActionButton = findViewById(R.id.floating_camera);
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        floatingActionButton.setOnClickListener(view -> {
            if (askForReadExternalStorage() && askForWriteExternalStorage() && askForStorage())
                callChooseFileFromDevice();
        });
        floatingCameraActionButton.setOnClickListener(view -> {
            if (askForCamera()) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });


    }

    private void callChooseFileFromDevice() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        String[] mimetypes = {"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/vnd.ms-excel"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        startActivityForResult(intent, READ_EXCEL_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_EXCEL_FILE && resultCode == RESULT_OK) {
            if (data != null) {
                Log.d("TEST", data.getData().toString());
                try {
                    readFromExcel(data.getData());
                } catch (IOException | InvalidFormatException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
//                File file;
//                try {
//                    file = new File(Objects.requireNonNull(PathUtil.getPath(peekAvailableContext(), data.getData())));
//                } catch (URISyntaxException e) {
//                    throw new RuntimeException(e);
//                }
//                if (file.isFile())
//                    Log.d(TAG, "onActivityResult: " + file);
//                try {
//                    readFromExcel(file).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe();
//                } catch (IOException | InvalidFormatException e) {
//                    throw new RuntimeException(e);
//                }
            }
        }
    }

    public void readFromExcel(Uri uri) throws IOException, InvalidFormatException, URISyntaxException {

        HSSFWorkbook myExcelBook;
        myExcelBook = new HSSFWorkbook(getContentResolver().openInputStream(uri));
        Sheet sheet = myExcelBook.getSheetAt(0);
        List<Diploma> diplomas = new ArrayList<>();
        for (Row row : sheet) {
            diplomas.add(new Diploma(row.getCell(0).getStringCellValue(),
                    row.getCell(1).getStringCellValue(),
                    row.getCell(2).getStringCellValue(),
                    (int) row.getCell(3).getNumericCellValue(),
                    row.getCell(4).getStringCellValue()));
        }
        myExcelBook.close();
        mainActivityViewModel.add(diplomas);
    }

    public boolean askForReadExternalStorage() {
        Log.d(TAG, "askForReadExternalStorage: brother moment");
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "askForReadExternalStorage");
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public boolean askForWriteExternalStorage() {
        Log.d(TAG, "askForReadExternalStorage: brother moment");
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "askForReadExternalStorage");
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
        return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public boolean askForStorage() {
        Log.d(TAG, "askForReadExternalStorage: brother moment");
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "askForReadExternalStorage");
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_STORAGE);
        }
        return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public boolean askForCamera() {
        Log.d(TAG, "askForReadExternalStorage: brother moment");
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "askForReadExternalStorage");
            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
        }
        return checkSelfPermission(Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }
}
