package com.example.memorybook;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class createPage extends AppCompatActivity {
    ImageView image;
    EditText baslik;
    EditText aciklama;
    Bitmap selectedImage;
    Button save;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_page);
        image=findViewById(R.id.imageView);
        baslik=findViewById(R.id.editText);
        aciklama=findViewById(R.id.editText2);
        save=findViewById(R.id.button);
    }

    public void selectImage(View view){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);

        }else{
            Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery,2);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery,2);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 2 && resultCode==RESULT_OK && data !=null){
            Uri imageData = data.getData();
            try {

                if(Build.VERSION.SDK_INT>=28){
                    ImageDecoder.Source source=ImageDecoder.createSource(this.getContentResolver(),imageData);
                    selectedImage=ImageDecoder.decodeBitmap(source);
                    image.setImageBitmap(selectedImage);
                }
                else{
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageData);
                    image.setImageBitmap(selectedImage);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void saveMemory(View view){

        String aniAciklama = aciklama.getText().toString();
        String aniBaslik = baslik.getText().toString();
        if (aniBaslik.equals("")) {
            aniBaslik = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }

        Bitmap smallImage=smallerImage(selectedImage,300);


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] bytes = outputStream.toByteArray();

        try {
            SQLiteDatabase database=this.openOrCreateDatabase("memoryBook",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS memory(id INTEGER PRIMARY KEY,baslik VARCHAR,aciklama VARCHAR, image BLOB)");

            String sqlString="INSERT INTO memory(baslik,aciklama,image) VALUES(?,?,?)";
            SQLiteStatement sqLiteStatement=database.compileStatement(sqlString);
            sqLiteStatement.bindString(1, aniBaslik);
            sqLiteStatement.bindString(2,aniAciklama);
            sqLiteStatement.bindBlob(3,bytes);
            sqLiteStatement.execute();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent=new Intent(createPage.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


        //finish();


    }

    public Bitmap smallerImage(Bitmap image,int maxSize){
        int width=image.getWidth();
        int height=image.getHeight();

        float bitmapRatio=(float) width / (float) height;
        if(bitmapRatio > 1) {
            width=maxSize;
            height=(int) (width/bitmapRatio);

        }
        else {
            height=maxSize;
            width=(int) (height*bitmapRatio);

        }
        return Bitmap.createScaledBitmap(image,width,height,true);

    }


}
