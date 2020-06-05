package com.example.memorybook;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class displayPage extends AppCompatActivity {
    ImageView imageView;
    TextView baslik;
    TextView aciklama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_page);
        imageView=findViewById(R.id.imageView2);
        baslik=findViewById(R.id.textView);
        aciklama=findViewById(R.id.textView2);



        Intent intent=getIntent();

        int memoryId=intent.getIntExtra("id",1);
        try {
            SQLiteDatabase database = this.openOrCreateDatabase("memoryBook", MODE_PRIVATE, null);

            Cursor cursor = database.rawQuery("SELECT * FROM memory WHERE id = ?", new String[] {String.valueOf(memoryId)});
            int baslikIx=cursor.getColumnIndex("baslik");
            int aciklamaIx=cursor.getColumnIndex("aciklama");
            int image=cursor.getColumnIndex("image");

            while (cursor.moveToNext()) {
                baslik.setText(cursor.getString(baslikIx));
                aciklama.setText(cursor.getString(aciklamaIx));

                byte [] bytes = cursor.getBlob(image);
                Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                imageView.setImageBitmap(bitmap);


            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

}
