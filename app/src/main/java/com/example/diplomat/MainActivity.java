package com.example.diplomat;

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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;
    private TextView textView;
    private MainActivityViewModel mainActivityViewModel;
    private FloatingActionButton floatingActionButton;
    private static final int READ_EXCEL_FILE = 1001;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1002;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 1004;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1005;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        textView = findViewById(R.id.scanResult);
        mCodeScanner = new CodeScanner(this, scannerView);

        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> mainActivityViewModel.getDiploma(Integer.parseInt(result.getText())).observe(MainActivity.this, diploma -> textView.setText(diploma.surname + ' ' + diploma.name + ' ' + diploma.patronymic + '\n' + diploma.subject + '\n' + diploma.olympiadName + '\n' + diploma.schoolName))));
        scannerView.setOnClickListener(view -> mCodeScanner.startPreview());
        floatingActionButton.setOnClickListener(view -> {
            callChooseFileFromDevice();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
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
            }
        }
    }

    private void readFromExcel(Uri uri) throws IOException, InvalidFormatException, URISyntaxException {

        XSSFWorkbook myExcelBook;
        myExcelBook = new XSSFWorkbook(getContentResolver().openInputStream(uri));
        Sheet sheet = myExcelBook.getSheetAt(0);
        List<Diploma> diplomas = new ArrayList<>();
        AtomicInteger c = new AtomicInteger();
        mainActivityViewModel.count().observe(MainActivity.this, c::set);
        Log.d(TAG, "readFromExcel: " + c.get());
        for (Row row : sheet) {
            diplomas.add(new Diploma(row.getCell(0).getStringCellValue(),
                    row.getCell(1).getStringCellValue(),
                    row.getCell(2).getStringCellValue(),
                    row.getCell(3).getStringCellValue(),
                    row.getCell(4).getStringCellValue(),
                    getIntent().getStringExtra("olimpiad"),
                    row.getCell(5).getStringCellValue()));
            Log.d(TAG, "readFromExcel: " + diplomas.get(0).token);
        }
        myExcelBook.close();
        mainActivityViewModel.add(diplomas);
        mainActivityViewModel.getDiplomas().observe(MainActivity.this, result -> {
            for (Diploma diploma: result) {
                Log.d(TAG, "readFromExcelsaidjaslkdj: " + diploma.token);
            }
        });
        generatePDF(diplomas, c.get() + 1);
    }

    private void generatePDF(List<Diploma> diplomas, int c) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint title = new Paint();
        Paint paint = new Paint();
        int g = 0;
        for (Diploma diploma: diplomas) {
            PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(630, 891, g).create();
            g += 1;
            PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);
            Canvas canvas = myPage.getCanvas();
            canvas.drawBitmap(generateQR(c), 0, 0, paint);
            title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            title.setColor(ContextCompat.getColor(this, R.color.black));

            title.setTextAlign(Paint.Align.CENTER);
            title.setTextSize(22);
            canvas.drawText(diploma.olympiadName, 315, 100, title);
            title.setTextSize(72);
            canvas.drawText("Диплом", 315, 300, title);
            title.setTextSize(32);
            canvas.drawText(diploma.surname + ' ' + diploma.name + ' ' + diploma.patronymic, 315, 500, title);
            canvas.drawText("по предмету", 315, 600, title);
            canvas.drawText(diploma.subject, 315, 650, title);
            c += 1;
            pdfDocument.finishPage(myPage);
        }
        File file = new File(Environment.getExternalStorageDirectory() + "/" + File.separator + "Diplomas.pdf");
        try {

            pdfDocument.writeTo(Files.newOutputStream(file.toPath()));

        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();
    }

    private Bitmap generateQR(int num) {
        QRCodeWriter writer = new QRCodeWriter();
        StringBuilder data = new StringBuilder(Integer.toString(num));
        while (data.length() != 8)
            data.insert(0, '0');
        try {
            Log.d(TAG, "generateQR: " + data);
            BitMatrix bitMatrix = writer.encode(data.toString(), BarcodeFormat.QR_CODE, 128, 128);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
    public boolean askForReadExternalStorage() {
        Log.d(TAG, "askForReadExternalStorage: brother moment");
        if (checkSelfPermission(READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "askForReadExternalStorage");
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
        return checkSelfPermission(READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }
    public boolean askForStorage() {
        Log.d(TAG, "askForReadExternalStorage: brother moment");
        if (checkSelfPermission(READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "askForReadExternalStorage");
            requestPermissions(new String[]{READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_STORAGE);
        }
        return checkSelfPermission(READ_EXTERNAL_STORAGE)
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
