package com.example.registerlogin;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.viewHolder>
{
    private ArrayList<EnrollItem> enrollItems;
    private Context context;
    private DBHelper mDBHelper;
    private TextView tv_title, tv_price;

    public CustomAdapter(ArrayList<EnrollItem> enrollItems, Context context) {
        this.enrollItems = enrollItems;
        this.context = context;
        mDBHelper = new DBHelper(context);
    }

    @NonNull
    @Override
    public CustomAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new viewHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.viewHolder holder, int position) {

        holder.tv_title.setText(enrollItems.get(position).getTitle());
        holder.tv_price.setText(enrollItems.get(position).getPrice());
    }

    @Override
    public int getItemCount() {
        return enrollItems.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        private TextView tv_title;
        private TextView tv_price;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_price = itemView.findViewById(R.id.tv_price);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int curPos = getAdapterPosition();
                    EnrollItem enrollItem = enrollItems.get(curPos);
                    String[] strChoiceItems = {"수정하기", "삭제하기", "구독정보 보기"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("원하는 작업을 선택 해주세요");
                    builder.setItems(strChoiceItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position)
                        {
                            if(position == 0) {
                                //수정하기
                                //팝업창 띄우기
                                Dialog dialog = new Dialog(context, android.R.style.Theme_Material_Light_Dialog);
                                dialog.setContentView(R.layout.dialog_edit);
                                EditText nameSpinner = dialog.findViewById(R.id.nameSpinner);
                                EditText et_price = dialog.findViewById(R.id.et_price);
                                Button date = dialog.findViewById(R.id.date);
                                Button btn_enroll = dialog.findViewById(R.id.btn_enroll);

                                nameSpinner.setText(enrollItem.getTitle());
                                et_price.setText(enrollItem.getPrice());
                                date.setText(enrollItem.getNextDate());

                                nameSpinner.setSelection(nameSpinner.getText().length());

                                Calendar c = Calendar.getInstance();
                                int mYear = c.get(Calendar.YEAR);
                                int mMonth = c.get(Calendar.MONTH);
                                int mDay = c.get(Calendar.DAY_OF_MONTH);

                                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
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
                                        String title = nameSpinner.getText().toString();
                                        String price = et_price.getText().toString();
                                        String nextDate = date.getText().toString();
                                        int id = enrollItem.getId();

                                        mDBHelper.UpdateEnroll(nameSpinner.getText().toString(), et_price.getText().toString(), date.getText().toString(), id);

                                        enrollItem.setTitle(title);
                                        enrollItem.setPrice(price);
                                        enrollItem.setNextDate(nextDate);
                                        notifyItemChanged(curPos, enrollItem);
                                        dialog.dismiss();
                                        Toast.makeText(context, "구독정보 수정이 완료 되었습니다.", Toast.LENGTH_SHORT).show();

                                    }
                                });
                                dialog.show();
                            }
                            else if(position == 1) {
                                //삭제하기
                                int id = enrollItem.getId();
                                mDBHelper.DeleteEnroll(id);

                                enrollItems.remove(curPos);
                                notifyItemRemoved(curPos);
                                Toast.makeText(context, "구독정보가 삭제되었습니다..", Toast.LENGTH_SHORT).show();

                            }
                            else if(position == 2) {
                                Dialog dialog = new Dialog(context, android.R.style.Theme_Material_Light_Dialog);
                                dialog.setContentView(R.layout.modify_item);
                                TextView ottNameText = dialog.findViewById(R.id.ottNameText);
                                TextView priceText2 = dialog.findViewById(R.id.priceText2);
                                TextView dateText2 = dialog.findViewById(R.id.dateText2);
                                Button button3 = dialog.findViewById(R.id.button3);

                                ottNameText.setText(enrollItem.getTitle());
                                priceText2.setText(enrollItem.getPrice());
                                dateText2.setText(enrollItem.getNextDate());

                                dialog.show();
                            }
                        }
                    });
                    builder.show();
                }
            });
        }
    }

    public void addItem(EnrollItem _item) {
        enrollItems.add(0, _item);
        notifyItemInserted(0);
    }
}
