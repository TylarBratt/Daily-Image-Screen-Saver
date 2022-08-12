package com.example.nasadailyimage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class ViewSaved extends AppCompatActivity{
    protected TextView tview = null;
    String filePath = "/storage/emulated/0/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_saved);

        Database db = MainActivity.database;
        ArrayList<String> list = getIntent().getStringArrayListExtra("List");
        RecyclerView rc = findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager;
        RecyclerView.Adapter adapter;

        layoutManager = new LinearLayoutManager(this);
        adapter = new MainAdapter(list);
        rc.setLayoutManager(layoutManager);
        rc.setAdapter(adapter);

        rc.addOnItemTouchListener(
                new RecyclerItemClickListener(this, rc, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        String date = list.get(position);
                        String newPath = filePath + date + ".png";
                        File file = new File(newPath);
                        Bitmap compressedImage = BitmapFactory.decodeFile(file.getAbsolutePath());

                        ImageView img = findViewById(R.id.imageView2);
                        img.setImageBitmap(compressedImage);
                    }

                })

        );

        Button button = findViewById(R.id.button4);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = findViewById(R.id.SearchDate);
                String date = editText.getText().toString();
                String found = "This date is saved!";
                int i = 0;
                for (String dates : list){
                    if(dates.equals(date)) {
                        i = 1;
                    }}

                    if (i == 1){
                        Snackbar.make(v,found, Snackbar.LENGTH_LONG).show();
                      //Toast toast = Toast.makeText(this, found, Toast.LENGTH_LONG);
                      //toast.show();
                        String newPath = filePath + date + ".png";
                        File file = new File(newPath);
                        Bitmap compressedImage = BitmapFactory.decodeFile(file.getAbsolutePath());
                        ImageView img = findViewById(R.id.imageView2);
                        img.setImageBitmap(compressedImage);
                    } else {
                        String notFound = "This date is not saved!";
                        Snackbar.make(v,notFound, Snackbar.LENGTH_LONG).show();
                    }

                }
            });
        }


}

