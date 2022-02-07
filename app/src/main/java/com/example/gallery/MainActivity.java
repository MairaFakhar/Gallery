package com.example.gallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    LinearLayout linearLayout;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_backup:
//                Intent intent = new Intent(MainActivity.this, Settings.class);
//                intent.putExtra("name", pic_name);
//                intent.putExtra("path", pic_path);
//                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Backup selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_restore:
                Toast.makeText(getApplicationContext(), "Restore selected", Toast.LENGTH_SHORT).show();
            case R.id.menu_log:
                Toast.makeText(getApplicationContext(), "Login/Logout selected", Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE},
                    101);
        }

        showImages(false);

        ImageButton sort_btn = findViewById(R.id.sort_btn);
        sort_btn.setOnClickListener(new View.OnClickListener() {

            boolean ascending = false;

            @Override
            public void onClick(View v){
                ascending = !ascending;
                showImages(ascending);
            }
        });
    }

    void showImages(boolean ascending)
    {
        linearLayout = findViewById(R.id.gallery_ll);
        linearLayout.removeAllViews();

        ArrayList<Picture> pictures = getAllImagesByFolder();

        if (ascending)
        {
            Collections.reverse(pictures);
        }

        String date = "";

        int i = 0;
        int sameRow = 0;

        while (i < pictures.size())
        {
            TableRow tr;
            if (!date.equals(pictures.get(i).getDate()))
            {
                tr = new TableRow(getApplicationContext());
                tr.setPadding(0, 15, 0, 15);
                TextView txt = new TextView(getApplicationContext());
                txt.setText(pictures.get(i).getDate());
                date = pictures.get(i).getDate();
                tr.addView(txt);
                linearLayout.addView(tr);
            }

            tr = new TableRow(getApplicationContext());
            tr.setPadding(0, 15, 0, 15);

            while (i < pictures.size() && date.equals(pictures.get(i).getDate()) && sameRow != 3)
            {
                date = pictures.get(i).getDate();
                ImageButton img = new ImageButton(getApplicationContext());
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                Bitmap bitmap = BitmapFactory.decodeFile(pictures.get(i).getPath(), bmOptions);
                bitmap = Bitmap.createScaledBitmap(bitmap, 300, 400, true);
                img.setImageBitmap(bitmap);
                img.setBackgroundColor(Color.WHITE);

                int space = (int)(Resources.getSystem().getDisplayMetrics().widthPixels / 3.2f);
                tr.addView(img, space, space);

                final String path = pictures.get(i).getPath();
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), path, Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(MainActivity.this, viewImage.class);
                        i.putExtra("path", path);
                        startActivity(i);
                    }
                });

                i++;
                sameRow++;
            }
            linearLayout.addView(tr);
            sameRow = 0;
        }
    }


    public ArrayList<Picture> getAllImagesByFolder()
    {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + '/';

        ArrayList<Picture> images = new ArrayList<>();
        Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DATE_TAKEN };
        Cursor cursor = this.getContentResolver().query(uri, projection, MediaStore.Images.Media.DATA + " like ? ", new String[] {"%"+path+"%"}, null);

        try
        {
            cursor.moveToFirst();
            do
            {
                Picture pic = new Picture();

                pic.setName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));
                pic.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
                pic.setSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));
                pic.setDate(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)));

                images.add(pic);
            }
            while(cursor.moveToNext());

            cursor.close();
            ArrayList<Picture> reSelection = new ArrayList<>();

            for (int i = images.size()-1; i > -1; i--)
            {
                reSelection.add(images.get(i));
            }
            images = reSelection;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return images;
    }

    public class Picture {

        private String name;
        private String path;
        private String size;
        private String uri;
        private Long date;

        public Picture(){

        }

        public Picture(String pictureName, String picturePath, String pictureSize, String imageUri) {
            this.name = pictureName;
            this.path = picturePath;
            this.size = pictureSize;
            this.uri = imageUri;
        }


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getUri() {
            return uri;
        }

        public void setUri(String uri) {
            this.uri = uri;
        }

        public Long getDateTime() {
            return date;
        }

        public String getDate() {
            Date d = new Date(this.date);
            SimpleDateFormat sd = new SimpleDateFormat("dd.MM.yyyy");
            return sd.format(d);
        }

        public void setDate(Long date) {
            this.date = date;
        }
    }
}