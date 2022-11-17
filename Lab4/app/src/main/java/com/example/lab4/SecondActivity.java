package com.example.lab4;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class SecondActivity extends AppCompatActivity {

    private static final String TAG = "SecondActivity";
    String phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            TextView ageView = findViewById(R.id.phoneView);
            phone = extras.getString(MainActivity.PHONE_KEY);
            ageView.setText(("Номер телефона: " +  phone));
        }
    }


    public void sendMessage(View view) {
        TextView textView = findViewById(R.id.phoneView);
        EditText editText = findViewById(R.id.smsView);
        textView.setText((textView.getText().toString() + "\nТекст сообщения:\n" + editText.getText().toString()));
    }

    /// Звонок
    public void call(View v) {
        String toDial="tel:" + phone;
        //if (ActivityCompat.checkSelfPermission(this,Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
        //    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(toDial)));
        //}else {
        //    final String[] PERMISSIONS_STORAGE = {Manifest.permission.CALL_PHONE};
        //    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CALL_PHONE}, 0);
        //}
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(toDial)));
    }

    /// Сообщение
    public void smsSend(View v) {
        EditText message= findViewById(R.id.smsView);
        String toSms="smsto:"+ phone;
        String messageText= message.getText().toString();
        Intent sms= new Intent(Intent.ACTION_SENDTO, Uri.parse(toSms));
        sms.putExtra("sms_body", messageText);
        //startActivity(sms);
        SmsManager.getDefault().sendTextMessage(phone, null, messageText, null, null);
    }

    /// Вернуться
    public void backFunc(View v) {
        Intent data = new Intent();
        EditText editText = findViewById(R.id.smsView);
        data.putExtra(MainActivity.ACCESS_MESSAGE, "Возврат " + editText.getText().toString());
        setResult(RESULT_OK, data);
        finish();
    }

    public void canselFunc(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }
}

