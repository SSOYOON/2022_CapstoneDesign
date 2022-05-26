package com.example.registerlogin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class FriendActivity extends AppCompatActivity implements View.OnClickListener{

    Button btn_Update;
    Button btn_Insert;
    Button btn_Select;
    EditText edit_ID;
    EditText edit_Name;
    EditText edit_Age;
    TextView text_ID;
    TextView text_Name;
    TextView text_Age;
    TextView text_Gender;
    CheckBox check_Man;
    CheckBox check_Woman;
    CheckBox check_ID;
    CheckBox check_Name;
    CheckBox check_Age;

    String ID;
    String name;
    long age;
    String gender = "";
    String sort = "id";

    ArrayAdapter<String> arrayAdapter;

    static ArrayList<String> arrayIndex = new ArrayList<>();
    static ArrayList<String> arrayData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);

        btn_Insert = findViewById(R.id.btn_insert);
        btn_Insert.setOnClickListener(this);
        btn_Update = findViewById(R.id.btn_update);
        btn_Update.setOnClickListener(this);
        btn_Select = findViewById(R.id.btn_select);
        btn_Select.setOnClickListener(this);
        edit_ID = findViewById(R.id.edit_id);
        edit_Name = findViewById(R.id.edit_name);
        edit_Age = findViewById(R.id.edit_age);
        text_ID = findViewById(R.id.text_id);
        text_Name = findViewById(R.id.text_name);
        text_Age = findViewById(R.id.text_age);
        text_Gender= findViewById(R.id.text_gender);
        check_Man = findViewById(R.id.check_man);
        check_Man.setOnClickListener(this);
        check_Woman= findViewById(R.id.check_woman);
        check_Woman.setOnClickListener(this);
        check_ID = findViewById(R.id.check_userid);
        check_ID.setOnClickListener(this);
        check_Name = findViewById(R.id.check_name);
        check_Name.setOnClickListener(this);
        check_Age = findViewById(R.id.check_age);
        check_Age.setOnClickListener(this);

        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        ListView listView = findViewById(R.id.db_list_view);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(onClickListener);
        listView.setOnItemLongClickListener(longClickListener);

        check_ID.setChecked(true);
        getFirebaseDatabase();

        btn_Insert.setEnabled(true);
        btn_Update.setEnabled(false);
    }

    public void setInsertMode(){
        edit_ID.setText("");
        edit_Name.setText("");
        edit_Age.setText("");
        check_Man.setChecked(false);
        check_Woman.setChecked(false);
        btn_Insert.setEnabled(true);
        btn_Update.setEnabled(false);
    }

    private final AdapterView.OnItemClickListener onClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.e("On Click", "position = " + position);
            Log.e("On Click", "Data: " + arrayData.get(position));
            String[] tempData = arrayData.get(position).split("\\s+");
            Log.e("On Click", "Split Result = " + Arrays.toString(tempData));
            edit_ID.setText(tempData[0].trim());
            edit_Name.setText(tempData[1].trim());
            edit_Age.setText(tempData[2].trim());
            if(tempData[3].trim().equals("Man")){
                check_Man.setChecked(true);
                gender = "Man";
            }else{
                check_Woman.setChecked(true);
                gender = "Woman";
            }
            edit_ID.setEnabled(false);
            btn_Insert.setEnabled(false);
            btn_Update.setEnabled(true);
        }
    };

    private final AdapterView.OnItemLongClickListener longClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("Long Click", "position = " + position);
            final String[] nowData = arrayData.get(position).split("\\s+");
            ID = nowData[0];
            String viewData = nowData[0] + ", " + nowData[1] + ", " + nowData[2] + ", " + nowData[3];
            AlertDialog.Builder dialog = new AlertDialog.Builder(FriendActivity.this);
            dialog.setTitle("데이터 삭제")
                    .setMessage("해당 데이터를 삭제 하시겠습니까?" + "\n" + viewData)
                    .setPositiveButton("네", (dialog1, which) -> {
                        postFirebaseDatabase(false);
                        getFirebaseDatabase();
                        setInsertMode();
                        edit_ID.setEnabled(true);
                        Toast.makeText(FriendActivity.this, "데이터를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("아니오", (dialog12, which) -> {
                        Toast.makeText(FriendActivity.this, "삭제를 취소했습니다.", Toast.LENGTH_SHORT).show();
                        setInsertMode();
                        edit_ID.setEnabled(true);
                    })
                    .create()
                    .show();
            return false;
        }
    };

    public boolean IsExistID(){
        return arrayIndex.contains(ID);
    }

    public void postFirebaseDatabase(boolean add){
        DatabaseReference mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if(add){
            FirebasePost post = new FirebasePost(ID, name, age, gender);
            postValues = post.toMap();
        }
        childUpdates.put("/id_list/" + ID, postValues);
        mPostReference.updateChildren(childUpdates);
    }

    public void getFirebaseDatabase(){
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("getFirebaseDatabase", "key: " + dataSnapshot.getChildrenCount());
                arrayData.clear();
                arrayIndex.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    String key = postSnapshot.getKey();
                    FirebasePost get = postSnapshot.getValue(FirebasePost.class);
                    assert get != null;
                    String[] info = {get.id, get.name, String.valueOf(get.age), get.gender};
                    String Result = setTextLength(info[0],10) + setTextLength(info[1],10) + setTextLength(info[2],10) + setTextLength(info[3],10);
                    arrayData.add(Result);
                    arrayIndex.add(key);
                    Log.d("getFirebaseDatabase", "key: " + key);
                    Log.d("getFirebaseDatabase", "info: " + info[0] + info[1] + info[2] + info[3]);
                }
                arrayAdapter.clear();
                arrayAdapter.addAll(arrayData);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("getFirebaseDatabase","loadPost:onCancelled", databaseError.toException());
            }
        };
        Query sortbyAge = FirebaseDatabase.getInstance().getReference().child("id_list").orderByChild(sort);
        sortbyAge.addListenerForSingleValueEvent(postListener);
    }

    public String setTextLength(String text, int length){
        if(text.length()<length){
            int gap = length - text.length();
            StringBuilder textBuilder = new StringBuilder(text);
            for (int i = 0; i<gap; i++){
                textBuilder.append(" ");
            }
            text = textBuilder.toString();
        }
        return text;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_insert:
                ID = edit_ID.getText().toString();
                name = edit_Name.getText().toString();
                age = Long.parseLong(edit_Age.getText().toString());
                if(!IsExistID()){
                    postFirebaseDatabase(true);
                    getFirebaseDatabase();
                    setInsertMode();
                }else{
                    Toast.makeText(FriendActivity.this, "이미 존재하는 ID 입니다. 다른 ID로 설정해주세요.", Toast.LENGTH_LONG).show();
                }
                edit_ID.requestFocus();
                edit_ID.setCursorVisible(true);
                break;

            case R.id.btn_update:
                ID = edit_ID.getText().toString();
                name = edit_Name.getText().toString();
                age = Long.parseLong(edit_Age.getText().toString());
                postFirebaseDatabase(true);
                getFirebaseDatabase();
                setInsertMode();
                edit_ID.setEnabled(true);
                edit_ID.requestFocus();
                edit_ID.setCursorVisible(true);
                break;

            case R.id.btn_select:
                getFirebaseDatabase();
                break;

            case R.id.check_man:
                check_Woman.setChecked(false);
                gender = "Man";
                break;

            case R.id.check_woman:
                check_Man.setChecked(false);
                gender = "Woman";
                break;

            case R.id.check_userid:
                check_Name.setChecked(false);
                check_Age.setChecked(false);
                sort = "id";
                break;

            case R.id.check_name:
                check_ID.setChecked(false);
                check_Age.setChecked(false);
                sort = "name";
                break;

            case R.id.check_age:
                check_ID.setChecked(false);
                check_Name.setChecked(false);
                sort = "age";
                break;
        }
    }
}