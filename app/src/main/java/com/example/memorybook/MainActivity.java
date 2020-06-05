package com.example.memorybook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<String> baslikArray;
    ArrayList<Integer> idArray;
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listView);
        baslikArray = new ArrayList<String>();
        idArray = new ArrayList<Integer>();

        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,baslikArray);
        listView.setAdapter(arrayAdapter);
        getData();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(MainActivity.this,displayPage.class);
                intent.putExtra("id",idArray.get(i));

                startActivity(intent);

            }
        });



    }

    public void getData(){
        try {
            SQLiteDatabase database = this.openOrCreateDatabase("memoryBook", MODE_PRIVATE, null);

            Cursor cursor = database.rawQuery("SELECT * FROM memory", null);
            int name = cursor.getColumnIndex("baslik");
            int id = cursor.getColumnIndex("id");

            while (cursor.moveToNext()) {
                baslikArray.add(cursor.getString(name));
                idArray.add(cursor.getInt(id));

                arrayAdapter.notifyDataSetChanged();

            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.add_memory,menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_memory_item){
            Intent intent=new Intent(MainActivity.this,createPage.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
