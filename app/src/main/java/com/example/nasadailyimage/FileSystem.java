package com.example.nasadailyimage;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileSystem {



    @RequiresApi(api = Build.VERSION_CODES.R)
    public void saveToFile(Bitmap bitmap, String date) throws FileNotFoundException {



    File path = Environment.getExternalStorageDirectory();
    System.out.println(path.toString());
    File f = new File(path, date + ".png");

        try (FileOutputStream out = new FileOutputStream( f)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap getFromFile(String date){
    Bitmap bitmap = BitmapFactory.decodeFile(date + ".png");
    return bitmap;
    }

}
