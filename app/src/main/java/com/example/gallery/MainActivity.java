package com.example.gallery;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    final int permissioncode = 999;
    final int CAMERA_REQUEST = 555;
    final int GALLERY_REQUEST = 666;
    LinearLayout ll;
    Uri imageUri;

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
//                intent.putExtra("city", CITY);
//                intent.putExtra("temp_type", TEMP);
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

        if(ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            // ask permission
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.CAMERA} ,
                    permissioncode);
        }

        if(ActivityCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE} ,
                    permissioncode);
        }
        Button cameraButton = findViewById(R.id.camerabutton);
        Button galleryButton = findViewById(R.id.buttongallery);
        //imageView = findViewById(R.id.imageView);
        ll = findViewById(R.id.ll);


        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                //intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select My Picture"),
                        GALLERY_REQUEST);

            }
        });


        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                //values.put(MediaStore.Images.Media.TITLE, "Test Photo Title");
                // values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                //
                imageUri = getContentResolver().insert( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, CAMERA_REQUEST);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent i)
    {
        super.onActivityResult(requestCode,resultCode, i);

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            Bitmap thumbnail;

            try {
                thumbnail = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                //imageView.setImageBitmap(thumbnail);
                ImageView img= new ImageView(MainActivity.this);
                img.setImageBitmap(thumbnail);
                ll.addView(img);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && null != i)
        {
            ClipData mClipData = i.getClipData();
            if (i.getData() != null)
            {
                Uri selectedImage = i.getData();
                Bitmap bitmap;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    //imageView.setImageBitmap(bitmap);
                    ImageView img= new ImageView(MainActivity.this);
                    img.setImageBitmap(bitmap);
                    ll.addView(img);

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }




    }
}