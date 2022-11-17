package com.example.lab4;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String CALL_PHONE = "CALL_PHONE";
    int cntOp = 0; /// число операций
    final static String nameVariableKey = "Число операций";

    private static final String PREFS_FILE = "Account";
    SharedPreferences settings;
    SharedPreferences.Editor prefEditor;

    EditText cntOpBox;
    EditText nameBox;
    EditText phoneBox;
    EditText ageBox;
    EditText rankBox;

    static final String ACCESS_MESSAGE="ACCESS_MESSAGE";
    static final String PHONE_KEY = "PHONE";

    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {

                TextView textView = findViewById(R.id.dataView);
                if(result.getResultCode() == Activity.RESULT_OK){
                    Intent intent = result.getData();
                    String accessMessage = intent.getStringExtra(ACCESS_MESSAGE);
                    textView.setText(accessMessage);
                }
                else{
                    textView.setText("Ошибка доступа");
                }
            });


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        phoneBox = findViewById(R.id.phoneBox);
        switch(id){
            case R.id.save_settings:
                Intent intent = new Intent(this, SecondActivity.class);
                intent.putExtra(PHONE_KEY, phoneBox.getText().toString());
                mStartForResult.launch(intent);
                return true;
        }
        //headerView.setText(item.getTitle());
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        settings = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);

        cntOpBox = findViewById(R.id.countBox);
        cntOpBox.setText(String.valueOf(settings.getInt("cntOp",1)));

        nameBox = findViewById(R.id.nameBox);
        nameBox.setText(settings.getString("name",""));

        phoneBox = findViewById(R.id.phoneBox);
        phoneBox.setText(settings.getString("phone",""));

        ageBox = findViewById(R.id.yearBox);
        ageBox.setText(String.valueOf(settings.getInt("age",21)));

        rankBox = findViewById(R.id.rankBox);
        rankBox.setText(settings.getString("rank",""));

    }

    // сохранение состояния
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(nameVariableKey, cntOp);
        super.onSaveInstanceState(outState);

    }
    // получение ранее сохраненного состояния
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        cntOp = savedInstanceState.getInt(nameVariableKey);
        TextView dataView = findViewById(R.id.dataView);
        dataView.setText(("Число операций: " + cntOp));
    }

    public void saveData(View view) {

        // получаем введенные данные
        cntOpBox = findViewById(R.id.countBox);
        try {
            cntOp = Integer.parseInt(cntOpBox.getText().toString());
        }catch (NumberFormatException ex) {
            cntOp = 0;
        }
    }


    public void getData(View view) {

        // получаем сохраненные данные
        TextView dataView = findViewById(R.id.dataView);
        dataView.setText(("Число операций: " + cntOp));
    }


    /// сохранение данных между активностями (при выходе из приложения)
    @Override
    protected void onPause(){
        super.onPause();

        cntOpBox = findViewById(R.id.countBox);
        nameBox = findViewById(R.id.nameBox);
        phoneBox = findViewById(R.id.phoneBox);
        ageBox = findViewById(R.id.yearBox);
        rankBox = findViewById(R.id.rankBox);

        // сохраняем в настройках
        prefEditor = settings.edit();

        prefEditor.putInt("cntOp", Integer.parseInt(cntOpBox.getText().toString()));
        prefEditor.putString("name", nameBox.getText().toString());
        prefEditor.putString("phone", phoneBox.getText().toString());
        prefEditor.putInt("age",  Integer.parseInt(ageBox.getText().toString()));
        prefEditor.putString("rank", rankBox.getText().toString());

        prefEditor.apply();
    }

}