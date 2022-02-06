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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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
                MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE} ,
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
                // values.put(MediaStore.Images.Media.TITLE, "Test Photo Title");
                // values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                //
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, CAMERA_REQUEST);

            }
        });

        ArrayList<pictureFacer> a = getAllImagesByFolder("a");
        for (int i = 0; i < a.size(); i++)
        {
            TableRow tr = new TableRow(getApplicationContext());

            TextView txt = new TextView(getApplicationContext());
            txt.setText(a.get(i).getPicturePath());
            txt.setMaxWidth(500);
            tr.addView(txt);

            ImageView img = new ImageView(getApplicationContext());
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            Bitmap bitmap = BitmapFactory.decodeFile(a.get(i).getPicturePath(),bmOptions);
            img.setImageBitmap(bitmap);
            tr.addView(img, 500, 500);

            ll.addView(tr);
        }
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
/*
    private ArrayList<imageFolder> getPicturePaths(){
        ArrayList<imageFolder> picFolders = new ArrayList<>();
        ArrayList<String> picPaths = new ArrayList<>();
        Uri allImagesuri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Images.ImageColumns.DATA ,MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,MediaStore.Images.Media.BUCKET_ID};
        Cursor cursor = this.getContentResolver().query(allImagesuri, projection, null, null, null);
        try {
            if (cursor != null) {
                cursor.moveToFirst();
            }
            do{
                imageFolder folds = new imageFolder();
                String name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                String folder = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String datapath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                //String folderpaths =  datapath.replace(name,"");
                String folderpaths = datapath.substring(0, datapath.lastIndexOf(folder+"/"));
                folderpaths = folderpaths+folder+"/";
                if (!picPaths.contains(folderpaths)) {
                    picPaths.add(folderpaths);

                    folds.setPath(folderpaths);
                    folds.setFolderName(folder);
                    folds.setFirstPic(datapath);//if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
                    folds.addpics();
                    picFolders.add(folds);
                }else{
                    for(int i = 0;i<picFolders.size();i++){
                        if(picFolders.get(i).getPath().equals(folderpaths)){
                            picFolders.get(i).setFirstPic(datapath);
                            picFolders.get(i).addpics();
                        }
                    }
                }
            }while(cursor.moveToNext());
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(int i = 0;i < picFolders.size();i++){
            Log.d("picture folders",picFolders.get(i).getFolderName()+" and path = "+picFolders.get(i).getPath()+" "+picFolders.get(i).getNumberOfPics());
        }

        //reverse order ArrayList

        ArrayList<imageFolder> reverseFolders = new ArrayList<>();

        for(int i = picFolders.size()-1;i > reverseFolders.size()-1;i--){
            reverseFolders.add(picFolders.get(i));
        }

        return picFolders;
    }
*/
    public ArrayList<pictureFacer> getAllImagesByFolder(String path){
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
        path = path+'/';
        ArrayList<pictureFacer> images = new ArrayList<>();
        Uri allVideosuri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Images.ImageColumns.DATA ,MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE};
        Cursor cursor = this.getContentResolver().query(allVideosuri, projection, MediaStore.Images.Media.DATA + " like ? ", new String[] {"%"+path+"%"}, null);

        try {
            cursor.moveToFirst();
            do {
                pictureFacer pic = new pictureFacer();

                pic.setPicturName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));

                pic.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));

                pic.setPictureSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));

                images.add(pic);
            } while(cursor.moveToNext());

            cursor.close();
            ArrayList<pictureFacer> reSelection = new ArrayList<>();

            for (int i = images.size()-1; i > -1; i--)
            {
                reSelection.add(images.get(i));
            }
            images = reSelection;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return images;
    }

    public class pictureFacer {

        private String picturName;
        private String picturePath;
        private  String pictureSize;
        private  String imageUri;
        private Boolean selected = false;

        public pictureFacer(){

        }

        public pictureFacer(String picturName, String picturePath, String pictureSize, String imageUri) {
            this.picturName = picturName;
            this.picturePath = picturePath;
            this.pictureSize = pictureSize;
            this.imageUri = imageUri;
        }


        public String getPicturName() {
            return picturName;
        }

        public void setPicturName(String picturName) {
            this.picturName = picturName;
        }

        public String getPicturePath() {
            return picturePath;
        }

        public void setPicturePath(String picturePath) {
            this.picturePath = picturePath;
        }

        public String getPictureSize() {
            return pictureSize;
        }

        public void setPictureSize(String pictureSize) {
            this.pictureSize = pictureSize;
        }

        public String getImageUri() {
            return imageUri;
        }

        public void setImageUri(String imageUri) {
            this.imageUri = imageUri;
        }

        public Boolean getSelected() {
            return selected;
        }

        public void setSelected(Boolean selected) {
            this.selected = selected;
        }
    }
}