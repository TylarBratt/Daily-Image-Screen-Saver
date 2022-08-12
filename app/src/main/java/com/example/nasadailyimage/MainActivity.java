package com.example.nasadailyimage;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    public static Database database;
    String globalDate;
    String globalImage;
    Boolean canSave = true;
    byte[] globalCompressedImage = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String baseUrl = "https://api.nasa.gov/planetary/apod?api_key=DgPLcIlnmN0Cwrzcg3e9NraFaYLIDI68Ysc6Zh3d&date=";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = new Database(this);

        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        DrawerLayout drawer = findViewById(R.id.drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, myToolbar, R.string.open, R.string.close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_bar);
        navigationView.setNavigationItemSelectedListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        CalendarView cal = (CalendarView) findViewById(R.id.calendarView);
        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                NasaImages nasaImages = new NasaImages();


                StringBuilder date = new StringBuilder();
                //this section of code builds the date into the correct format
                date.append(year);
                date.append("-");
                if (month - 9 <= 1) {
                    date.append("0");
                }
                date.append(month);
                date.append("-");
                if (dayOfMonth - 9 <= 1) {
                    date.append("0");
                }
                date.append(dayOfMonth);
                globalDate = date.toString();

                //this section is used to populate the date to the first textview
                TextView dateText = findViewById(R.id.Date);
                dateText.setText("Date: " + date);


                //this creates the url to get our image.
                StringBuilder url = new StringBuilder();
                url.append(baseUrl);
                url.append(date.toString());

                //this populates the second textview
                TextView urlText = findViewById(R.id.URL);
                urlText.setText("URL: " + url.toString());

                //this executes the url in the background and sets the Image textview
                String image = null;
                try {
                    image = database.getImage(date.toString());
                } catch (Exception e) {
                    image = "No Image";
                }
                if (image == "No Image") {
                    nasaImages.execute(url.toString());
                } else {
                    TextView imageText = findViewById(R.id.Image);
                    imageText.setText(image);

                    canSave = false;
                }

            }
        });

        Button save = findViewById(R.id.button);
        Button view = findViewById(R.id.button2);
        Button viewSaved = findViewById(R.id.button3);
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (canSave = true) {
                    DatabaseImages dbImages = new DatabaseImages();
                    dbImages.execute("start");
                } else {
                    Toast toast = Toast.makeText(MainActivity.this, "Image already saved to Database", Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap compressedImage = database.getCompressedImage(globalDate);

                if (compressedImage == null) {
                    Uri uriUrl = Uri.parse(globalImage);
                    Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                    startActivity(launchBrowser);
                } else {
                    Intent openImage = new Intent(MainActivity.this, ViewImage.class);
                    openImage.putExtra("Image", compressedImage);
                    startActivity(openImage);

                }
            }
        });
        viewSaved.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             Intent viewSaved = new Intent(MainActivity.this, ViewSaved.class);
                                             ArrayList<String> list = database.getSavedImages();
                                             viewSaved.putExtra("List", list);
                                             startActivity(viewSaved);

                                         }
                                     }


        );
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        String message = null;

        switch(item.getItemId())
        {
            case R.id.drawerItem1:
                message = "You clicked Exit";
                System.exit(0);
                break;
            case R.id.drawerItem2:
                message = "You clicked on Saved Images";
                startActivity(new Intent(MainActivity.this, ViewSaved.class));
                break;
            case R.id.drawerItem3:
                message = "You clicked on Home";
                break;

        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer);
        drawerLayout.closeDrawer(GravityCompat.START);

        Snackbar.make(findViewById(R.id.constraintLayout),"NavigationDrawer: " + message, Snackbar.LENGTH_LONG).show();
       //Toast.makeText(this, "NavigationDrawer: " + message, Toast.LENGTH_LONG).show();
        return false;
    }


        public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu, menu);
            return true;
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            String message = null;
            //Look at your menu XML file. Put a case for every id in that file:
            switch(item.getItemId()) {
                //what to do when the menu item is selected:
                case R.id.item1:
                    message = "You clicked on item 1";
                    break;

                case R.id.item2:
                    message = "You clicked on item 2";
                    startActivity(new Intent(MainActivity.this, ViewSaved.class));
                    break;

                case R.id.item3:
                    message = "You clicked on item 3";
                    break;

            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            return true;
        }






private class NasaImages extends AsyncTask<String, Integer, String> {

    @Override
    protected String doInBackground(String[] params) {

        InputStream stream = getURL(params[0]);
        String mediator = null;
        try {
            mediator = JSONReader(stream);
        } catch (IOException e) {
            System.out.println("Failed to create JSON String");
            e.printStackTrace();
        }
        String[] a = mediator.split("\"hdurl\":\"");
        String[] b = a[1].split("\",\"media_type\"");


        for (int j = 0; j < 100; j++) {
            try {
                publishProgress(j);
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return b[0];
    }
    protected void onPostExecute(String fromDoInBackground){
        Log.i("HTTP", fromDoInBackground);
        TextView tview = findViewById(R.id.Image);
        tview.setText("Image: " + fromDoInBackground);
        globalImage = fromDoInBackground;
        Log.i("HTTP", "Done");
    }

    protected void onProgressUpdate(Integer ... args){


        ProgressBar pb = findViewById(R.id.progressBar);

                pb.setProgress(args[0]);

        }
    }



    private class DatabaseImages extends AsyncTask<String, byte[], String> {
        int width;
        int height;

        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;//"http://192.xx.xx.xx/mypath/img1.jpg
            byte[] compImage = null;
             int responseCode = 0;
             HttpURLConnection con = null;
            try {
                url = new URL(globalImage);
                con = (HttpURLConnection)url.openConnection();
                con.connect();
                 responseCode = con.getResponseCode();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(responseCode == HttpURLConnection.HTTP_OK)
            {
                //download
                InputStream in = null;
                Bitmap bmp = null;
                try {
                    in = con.getInputStream();
                    bmp = BitmapFactory.decodeStream(in);
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);

                FileSystem filesystem = new FileSystem();
                try {
                    filesystem.saveToFile(bmp, globalDate);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                height = bmp.getHeight();
                width = bmp.getWidth();
                int test = (bmp.getRowBytes()*bmp.getHeight()*4);
                onProgressUpdate(bos.toByteArray());
            }
            return "done";
        }
        protected void onProgressUpdate(byte[] array){
            globalCompressedImage = array;
            database.loadToDatabase(globalDate, globalImage, array, height, width);


        }}

    //the below methods are utilities
    public InputStream getURL(String param){
        InputStream response = null;
        try{
            URL url = new URL(param);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            response = urlConnection.getInputStream();
            return response;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
    public String JSONReader(InputStream inputStream) throws IOException {
        String result;
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line + "\n");
        }
        result = stringBuilder.toString();
        reader.close();
        return result;
    }

}