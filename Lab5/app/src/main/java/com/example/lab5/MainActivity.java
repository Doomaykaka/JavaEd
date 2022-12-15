package com.example.lab5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    DataHelper databaseHelper;
    ListView userList;
    SQLiteDatabase db;
    TextView header;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    Button updButton;
    Button rndButton;
    Button uprButton;
    EditText updText;
    TextView txtOut;
    Button filterButton;
    ListView filterList;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        header = findViewById(R.id.header);
        userList = findViewById(R.id.list);

        updButton = findViewById(R.id.button);
        updText = findViewById(R.id.editTextTextPersonName);

        rndButton = findViewById(R.id.button3);
        uprButton = findViewById(R.id.button2);

        txtOut = findViewById(R.id.textView);

        filterButton = findViewById(R.id.button4);
        filterList = findViewById(R.id.flist);

        databaseHelper = new DataHelper(getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        db = databaseHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DataHelper.COLUMN_NAME,"Dmitriy");
        cv.put(DataHelper.COLUMN_YEAR, 2002);
        cv.put(DataHelper.COLUMN_AGE, 22);
        cv.put(DataHelper.COLUMN_RANK, "V");
        db.insert(DataHelper.TABLE, null, cv);
        userCursor =  db.rawQuery("select * from "+ DataHelper.TABLE, null);
        userCursor.moveToFirst();
        for(int i=0;i<userCursor.getCount();i++){
            int year = userCursor.getInt(2);
            int age = userCursor.getInt(3);
            if((2022-year)!=age){
                System.out.println("Wrong age "+i+" , age data="+age+" , real age="+(2022-year));
                txtOut.setText("Wrong age "+i+" , age data="+age+" , real age="+(2022-year));
            }
            userCursor.moveToNext();
        }
        String[] headers = new String[] {DataHelper.COLUMN_NAME, DataHelper.COLUMN_YEAR};
        userAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                userCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        header.setText("Найдено элементов: " +  userCursor.getCount());
        userList.setAdapter(userAdapter);

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                id=i+1;
            }
        });

        updButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(id>-1){
                    db.execSQL("UPDATE "+DataHelper.TABLE+" SET "+DataHelper.COLUMN_NAME+"=\'"+updText.getText()+"\' WHERE _id="+id+";");
                    userCursor = db.rawQuery("select * from "+ DataHelper.TABLE, null);
                    userAdapter.notifyDataSetChanged();
                }
            }
        });

        rndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int age = -1;
                userCursor =  db.rawQuery("select * from "+ DataHelper.TABLE, null);
                userCursor.moveToFirst();
                for(int i=0;i<userCursor.getCount();i++){
                    if(userCursor.getInt(0)==id){
                        age = userCursor.getInt(3);
                    }
                    userCursor.moveToNext();
                }
                if(age!=-1){
                    userCursor = db.rawQuery("select round("+age+".1,-1) AS avg FROM spaceman" , null);
                    userCursor.moveToFirst();
                    int agerounded = userCursor.getInt(0);
                    System.out.println("Rounded age "+Integer.toString(agerounded));
                    txtOut.setText("Rounded age "+Integer.toString(agerounded));
                }
            }
        });

        uprButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = "";
                userCursor =  db.rawQuery("select * from "+ DataHelper.TABLE, null);
                userCursor.moveToFirst();
                for(int i=0;i<userCursor.getCount();i++){
                    if(userCursor.getInt(0)==id) {
                        name = userCursor.getString(1);
                    }
                    userCursor.moveToNext();
                }

                userCursor = db.rawQuery("select UPPER(name) FROM spaceman where rank=(select rank from spaceman where _id="+id+")" , null);
                userCursor.moveToFirst();
                if(!name.equals("")){
                    String upper = userCursor.getString(0);
                    System.out.println("Upper "+upper);
                    txtOut.setText("Upper "+upper);
                }
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = updText.getText().toString();
                if(s!=null){
                    userAdapter.getFilter().filter(s.toString());
                    userList.setAdapter(userAdapter);
                    userList.deferNotifyDataSetChanged();
                }
            }
        });

        // данная проверка нужна при переходе от одной ориентации экрана к другой
        if(!updText.getText().toString().isEmpty())
            userAdapter.getFilter().filter(updText.getText().toString());

        userAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {

                if (constraint == null || constraint.length() == 0) {
                    return db.rawQuery("select * from " + DataHelper.TABLE, null);
                }
                else {
                    return db.rawQuery("select * from " + DataHelper.TABLE + " where " +
                            DataHelper.COLUMN_NAME + " like ?", new String[]{"%" + constraint.toString() + "%"});
                }
            }
        });

    }

    public void onDestroy(){
        super.onDestroy();
        db.close();
        userCursor.close();
    }
}