package com.jimmy.ebac_estimate;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.jimmy.ebac_estimate.RoomDataBase.Data;
import com.jimmy.ebac_estimate.RoomDataBase.DataBase;
import com.jimmy.ebac_estimate.function.CalculateEBAC;
import com.jimmy.ebac_estimate.function.CalculateSD;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddDrinkActivity2 extends AppCompatActivity {
    String id,abv,ml,comment;
    SimpleDateFormat DateFormatter = new SimpleDateFormat("Y年M月d日");
    SimpleDateFormat TimeFormatter = new SimpleDateFormat("a hh:mm");
    Calendar nowTime;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drink2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_drink2);
        toolbar.setTitle("新增酒杯");
        setSupportActionBar(toolbar);


        EditText TextDrinkName = findViewById(R.id.editText_drinkName);
        EditText TextDrinkAbv = findViewById(R.id.editText_drinkAbv);
        EditText TextDrinkML = findViewById(R.id.editText_drinkML);

        Button BtAdd = findViewById(R.id.buttonAdd);
        Button BtCancel = findViewById(R.id.buttonCancel);
        TextView DrinkDate = findViewById(R.id.drinkDate);
        TextView DrinkTime = findViewById(R.id.drinkTime);
        TextView Comment = findViewById(R.id.comment);

        //擷取ID,ABV,ML
        Intent intent=getIntent();
        id=intent.getStringExtra("id");
        abv=intent.getStringExtra("abv");
        ml=intent.getStringExtra("ml");
        comment = intent.getStringExtra("comment");


        abv=abv.substring(0,abv.indexOf("%"));
        ml=ml.substring(0,ml.indexOf("ml"));

        TextDrinkName.setText(id);
        TextDrinkAbv.setText(abv);
        TextDrinkML.setText(ml);

        if (comment.equals("no")) {
            Comment.setText(" ");
        }
        else {
            Comment.setText(comment);
        }


        nowTime = Calendar.getInstance();
        DrinkDate.setText(DateFormatter.format(nowTime.getTime()));
        DrinkTime.setText(TimeFormatter.format(nowTime.getTime()));

        /**set date picker*/
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                nowTime.set(Calendar.YEAR,year);
                nowTime.set(Calendar.MONTH,month);
                nowTime.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                DrinkDate.setText(DateFormatter.format(nowTime.getTime()));
            }

        },nowTime.get(Calendar.YEAR),nowTime.get(Calendar.MONTH),nowTime.get(Calendar.DAY_OF_MONTH));

        /**set time picker*/
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                nowTime.set(Calendar.HOUR_OF_DAY,hourOfDay);
                nowTime.set(Calendar.MINUTE,minute);
                DrinkTime.setText(TimeFormatter.format(nowTime.getTime()));
            }
        },nowTime.get(Calendar.HOUR_OF_DAY),nowTime.get(Calendar.MINUTE),false);




        /**新增按鈕事件*/
        BtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(() -> {
                String DrinkName = TextDrinkName.getText().toString();
                String DrinkAbv = TextDrinkAbv.getText().toString();
                String DrinkML = TextDrinkML.getText().toString();

                GlobalVariable userdata = (GlobalVariable)getApplicationContext();
                CalculateSD SD = new CalculateSD(Float.parseFloat(DrinkML),Float.parseFloat(DrinkAbv));

                CalculateEBAC nowValueEBAC = new CalculateEBAC(SD.getSD(),userdata.getBW(),userdata.getWt(),0);
                int Time = nowValueEBAC.getTime();
                Calendar soberTime = (Calendar) nowTime.clone();
                soberTime.add(Calendar.MINUTE,Time);

                if (DrinkName.length() == 0) return;//如果名字欄沒填入任何東西，則不執行下面的程序
                Data data = new Data(DrinkName,DrinkML,DrinkAbv,Float.toString(SD.getSD()),nowTime,soberTime);
                DataBase.getInstance(AddDrinkActivity2.this).getDataUao().insertData(data);

                }).start();

                Intent intent = new Intent(AddDrinkActivity2.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

        /**取消按鈕事件*/
        BtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddDrinkActivity2.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });

    }
    public void changeDate(View v){
        datePickerDialog.show();
    }

    public void changeTime(View v){
        timePickerDialog.show();
    }
}
