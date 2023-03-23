package com.example.diplomat;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        floatingActionButton = findViewById(R.id.floating);
        recyclerView = findViewById(R.id.notesRecycler);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callChooseFileFromDevice();
            }
        });

    }
    private void callChooseFileFromDevice() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            if (data != null) {
                Log.d(TAG, "onActivityResult: " + data.getData());
            }
        }
    }
}