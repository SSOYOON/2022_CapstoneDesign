package com.example.registerlogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CancelActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel);
        Button btn_call = findViewById(R.id.btn_call);
        Button btn_threeidiotscoding = findViewById(R.id.btn_threeidiotscoding);


    }
    public void onClick(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        switch (v.getId()){
            case R.id.btn_call:
                intent.setData(Uri.parse("tel:080-001-9587"));
                startActivity(intent);
                break;
            case R.id.btn_threeidiotscoding:
                intent.setData(Uri.parse("https://www.netflix.com/kr/"));
                startActivity(intent);
                break;
        }
    }
}