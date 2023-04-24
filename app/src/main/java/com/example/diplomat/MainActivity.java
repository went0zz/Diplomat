package com.example.diplomat;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int READ_EXCEL_FILE = 1001;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1002;
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
            if (askForReadExternalStorage() && askForStorage())
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
            }
        }
    }

    private void readFromExcel(Uri uri) throws IOException, InvalidFormatException, URISyntaxException {

        XSSFWorkbook myExcelBook;
        myExcelBook = new XSSFWorkbook(getContentResolver().openInputStream(uri));
        Sheet sheet = myExcelBook.getSheetAt(0);
        List<Diploma> diplomas = new ArrayList<>();
        for (Row row : sheet) {
            diplomas.add(new Diploma(Objects.requireNonNull(mainActivityViewModel.getDiplomas().getValue()).size(), row.getCell(0).getStringCellValue(),
                    row.getCell(1).getStringCellValue(),
                    row.getCell(2).getStringCellValue(),
                    (int) row.getCell(3).getNumericCellValue(),
                    row.getCell(4).getStringCellValue()));
        }
        myExcelBook.close();
        mainActivityViewModel.add(diplomas);
        generatePDF(diplomas);
    }

    private void generatePDF(List<Diploma> diplomas) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint title = new Paint();

        int c = 1;
        for (Diploma diploma: diplomas) {
            PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(1120, 792, c).create();
            PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);
            Canvas canvas = myPage.getCanvas();
            title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
            title.setColor(ContextCompat.getColor(this, R.color.purple_200));
            title.setTextSize(15);
            title.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(diploma.surname + ' ' + diploma.name + ' ' + diploma.pantonymic + ' ' + diploma.place + ' ' + diploma.schoolid, 396, 560, title);
            c++;
            pdfDocument.finishPage(myPage);
        }
        Log.d(TAG, "generatePDF: drista");

        File file = new File(Environment.getExternalStorageDirectory() + "/" + File.separator + "Diplomas.pdf");
        Log.d(TAG, "generatePDF: huyyy");
        try {

            pdfDocument.writeTo(Files.newOutputStream(file.toPath()));
            Log.d(TAG, "generatePDF: fole");

        } catch (IOException e) {
            e.printStackTrace();
        }
        pdfDocument.close();
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
    private Bitmap generateQRCode(int id) {
        StringBuilder data = new StringBuilder(String.valueOf(id));
        while (data.length() != 8)
            data.insert(0, '0');
        if (TextUtils.isEmpty(data.toString())) {

            // if the edittext inputs are empty then execute
            // this method showing a toast message.
            Toast.makeText(MainActivity.this, "Enter some text to generate QR Code", Toast.LENGTH_SHORT).show();
        } else {
            // below line is for getting
            // the windowmanager service.
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);

            // initializing a variable for default display.
            Display display = manager.getDefaultDisplay();

            // creating a variable for point which
            // is to be displayed in QR Code.
            Point point = new Point();
            display.getSize(point);

            // getting width and
            // height of a point
            int width = point.x;
            int height = point.y;

            // generating dimension from width and height.
            int dimen = Math.min(width, height);
            dimen = dimen * 3 / 4;
            // setting this dimensions inside our qr code
            // encoder to generate our qr code.
            qrgEncoder = new QRGEncoder(data.toString(), null, QRGContents.Type.TEXT, dimen);
            try {
                // getting our qrcode in the form of bitmap.
                bitmap = qrgEncoder.encodeAsBitmap();
                // the bitmap is set inside our image
                // view using .setimagebitmap method.
                qrCodeIV.setImageBitmap(bitmap);
            } catch (WriterException e) {
                // this method is called for
                // exception handling.
                Log.e("Tag", e.toString());
            }
        }
    }
}
