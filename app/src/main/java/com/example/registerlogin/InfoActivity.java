package com.example.registerlogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;

public class InfoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{

    private RecyclerView rv_enroll;
    private FloatingActionButton floatingActionButton;
    private ArrayList<EnrollItem> enrollItems;
    private DBHelper mDBHelper;
    private CustomAdapter mAdapter;
    private Button btn_push;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        btn_push = findViewById(R.id.btn_push);

        btn_push.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        setInit();
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day){
        Calendar c = Calendar.getInstance();
        // 알림 받을 날짜 선택 후 set - 그 날 아침 9시에 알림 가도록 임시 설정
        c.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 14, 35, 0);

        startAlarm(c);
    }

    private void startAlarm(Calendar c){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (c.before(Calendar.getInstance())) {
            Toast.makeText(this, "알람시간이 현재시간보다 이전일 수 없습니다.", Toast.LENGTH_LONG).show();
            return;
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }


    private void setInit()
    {
        mDBHelper = new DBHelper(this);
        rv_enroll = findViewById(R.id.rv_enroll);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        enrollItems = new ArrayList<>();

        loadRencentDB();

        floatingActionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                Dialog dialog = new Dialog(InfoActivity.this, android.R.style.Theme_Material_Light_Dialog);
                dialog.setContentView(R.layout.dialog_edit);
                EditText nameSpinner = dialog.findViewById(R.id.nameSpinner);
                EditText et_price = dialog.findViewById(R.id.et_price);
                Button date = dialog.findViewById(R.id.date);
                Button btn_enroll = dialog.findViewById(R.id.btn_enroll);

                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(InfoActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.setText(year + "/" + (month+1) + "/" + dayOfMonth);
                    }
                }, mYear, mMonth, mDay);

                date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (date.isClickable()) {
                            datePickerDialog.show();
                        }
                    }
                });

                btn_enroll.setOnClickListener(new View.OnClickListener()
                {

                    @Override
                    public void onClick(View view)
                    {
                        mDBHelper.InsertEnroll(nameSpinner.getText().toString(), et_price.getText().toString(), date.getText().toString());
                        //Insert UI

                        EnrollItem item = new EnrollItem();
                        item.setTitle(nameSpinner.getText().toString());
                        item.setPrice(et_price.getText().toString());
                        item.setNextDate(date.getText().toString());

                        mAdapter.addItem(item);
                        rv_enroll.smoothScrollToPosition(0);
                        dialog.dismiss();
                        Toast.makeText(InfoActivity.this, "구독 목록에 추가 되었습니다 !",Toast.LENGTH_SHORT).show();

                    }
                });
                dialog.show();
            }
        });

    }

    private void loadRencentDB() {
        //저장되었던 DB를 가져온다.
        enrollItems = mDBHelper.getEnrollList();
        if(mAdapter == null) {
            mAdapter = new CustomAdapter(enrollItems, this);
            rv_enroll.setHasFixedSize(true);
            rv_enroll.setAdapter(mAdapter);
        }
    }
}