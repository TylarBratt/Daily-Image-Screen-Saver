package com.example.nasadailyimage;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class ViewImage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        //This is a new activity to view an image. We pass a byte stream from the previous activity in and convert it to a bitmap which is then displayed on the Imageview.

        byte[] compressedImage = getIntent().getByteArrayExtra("Image");
        Bitmap image = BitmapFactory.decodeByteArray(compressedImage, 0, compressedImage.length);
        ImageView img = findViewById(R.id.imageView);
        img.setImageBitmap(image);



        //A button needs to be employed to go back.
    }
}