package com.example.lab3;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    int listId=-1;

    // набор данных, которые свяжем со списком
    ArrayList<String> epithets = new ArrayList<String>();
    ArrayList<String> nouns = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // заполняем
        epithets.add("Легендарный(ая)");
        epithets.add("Известный(ая)");
        epithets.add("Малоизвестный(ая)");
        epithets.add("Молодой(ая)");
        epithets.add("Смелый(ая)");
        nouns.add("космонавт");
        nouns.add("космонавтка");

        // получаем элемент
        TextView selection = findViewById(R.id.selection);
        Button myButton = (Button) findViewById(R.id.button);
        CheckBox myCheck = (CheckBox) findViewById(R.id.checkBox);

        Button edit =  (Button) findViewById(R.id.button2);
        EditText editText = (EditText) findViewById(R.id.newvalue);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radios);

        Button delete = (Button)  findViewById(R.id.button3);
        EditText deleteText = (EditText) findViewById(R.id.delvalue);

        Button editElem = (Button)  findViewById(R.id.button4);
        EditText editId = (EditText) findViewById(R.id.editid);
        EditText editValue = (EditText) findViewById(R.id.editvalue);

        ListView epithetsList = findViewById(R.id.epithetsList);
        Spinner spinner = findViewById(R.id.spinner);

        // создаем адаптер
        ArrayAdapter<String> adapter_list = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, epithets);
        ArrayAdapter<String> adapter_spinner = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, nouns);

        adapter_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // устанавливаем для списка адаптер
        epithetsList.setAdapter(adapter_list);
        spinner.setAdapter(adapter_spinner);

        epithetsList.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                listId = position;
            }
        });

        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get selected radio button from radioGroup
                int selectedIdRadio = radioGroup.getCheckedRadioButtonId();
                // find the radiobutton by returned id
                RadioButton radioButton = (RadioButton) findViewById(selectedIdRadio);

                int selectedIdSpinner = (int)spinner.getSelectedItemId();

                String result = epithets.toArray()[listId] + " "  + nouns.toArray()[selectedIdSpinner] + " " + radioButton.getText();

                boolean isUpper = myCheck.isChecked();

                if(isUpper){
                    result=result.toUpperCase(Locale.ROOT);
                }

                selection.setText(result);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String res = editText.getText().toString();

                epithets.add(res);
                adapter_list.notifyDataSetChanged();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = deleteText.getText().toString();

                epithets.remove(Integer.parseInt(id));
                adapter_list.notifyDataSetChanged();
            }
        });

        editElem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = editId.getText().toString();
                String value = editValue.getText().toString();

                epithets.remove(Integer.parseInt(id));
                epithets.add(value);
                adapter_list.notifyDataSetChanged();
            }
        });
    }
}