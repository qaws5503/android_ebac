package com.jimmy.ebac_estimate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jimmy.ebac_estimate.RoomDataBase.Data;
import com.jimmy.ebac_estimate.RoomDataBase.DataBase;
import com.jimmy.ebac_estimate.function.CalculateEBAC;
import com.jimmy.ebac_estimate.function.CalculateSD;
import com.jimmy.ebac_estimate.function.CalendarStringConvert;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ModifyDrink extends AppCompatActivity {

    private int Id;
    private float BW,Wt;
    private String DrinkAbv, DrinkML;
    private CalendarStringConvert Convert = new CalendarStringConvert();
    private Calendar InitialStartDrinkTime,SoberDrinkTime;
    private Calendar ChangeStartDrinkTime;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    SimpleDateFormat DateFormatter = new SimpleDateFormat("Y年M月d日");
    SimpleDateFormat TimeFormatter = new SimpleDateFormat("a hh:mm");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_drink);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_modify_drink);
        toolbar.setTitle("修改酒杯");


        EditText TextDrinkName = findViewById(R.id.editText_md_drinkName);
        EditText TextDrinkAbv = findViewById(R.id.editText_md_drinkAbv);
        EditText TextDrinkML = findViewById(R.id.editText_md_drinkML);

        TextView DrinkDate = findViewById(R.id.md_drinkDate);
        TextView DrinkTime = findViewById(R.id.md_drinkTime);

        GlobalVariable userdata = (GlobalVariable)getApplicationContext();
        BW = userdata.getBW();
        Wt = userdata.getWt();

        Intent intent=getIntent();
        String DrinkName =  intent.getStringExtra("DrinkName");
        DrinkAbv =  intent.getStringExtra("DrinkAbv");
        DrinkML =  intent.getStringExtra("DrinkML");
        Id = intent.getIntExtra("Id",0);

        InitialStartDrinkTime =  Convert.StringToCalendar(intent.getStringExtra("startDrinkTime"));
        SoberDrinkTime =  Convert.StringToCalendar(intent.getStringExtra("SoberDrinkTime"));


        TextDrinkName.setText(DrinkName);
        TextDrinkAbv.setText(DrinkAbv);
        TextDrinkML.setText(DrinkML);


        DrinkDate.setText(DateFormatter.format(InitialStartDrinkTime.getTime()));
        DrinkTime.setText(TimeFormatter.format(InitialStartDrinkTime.getTime()));

        ChangeStartDrinkTime = (Calendar) InitialStartDrinkTime.clone();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                ChangeStartDrinkTime.set(Calendar.YEAR,year);
                ChangeStartDrinkTime.set(Calendar.MONTH,month);
                ChangeStartDrinkTime.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                DrinkDate.setText(DateFormatter.format(ChangeStartDrinkTime.getTime()));
            }

        },ChangeStartDrinkTime.get(Calendar.YEAR),ChangeStartDrinkTime.get(Calendar.MONTH),ChangeStartDrinkTime.get(Calendar.DAY_OF_MONTH));


        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                ChangeStartDrinkTime.set(Calendar.HOUR_OF_DAY,hourOfDay);
                ChangeStartDrinkTime.set(Calendar.MINUTE,minute);
                DrinkTime.setText(TimeFormatter.format(ChangeStartDrinkTime.getTime()));
            }
        },ChangeStartDrinkTime.get(Calendar.HOUR_OF_DAY),ChangeStartDrinkTime.get(Calendar.MINUTE),false);

    }

    public void Modify(View view){
        new Thread(() -> {
            EditText TextDrinkName = findViewById(R.id.editText_md_drinkName);
            EditText TextDrinkAbv = findViewById(R.id.editText_md_drinkAbv);
            EditText TextDrinkML = findViewById(R.id.editText_md_drinkML);

            String drinkName = TextDrinkName.getText().toString();
            String drinkAbv = TextDrinkAbv.getText().toString();
            String drinkML = TextDrinkML.getText().toString();



            CalculateSD SD = new CalculateSD(Float.parseFloat(drinkML),Float.parseFloat(drinkAbv));
            if (DrinkAbv != drinkAbv || DrinkML != drinkML){
                CalculateEBAC EBAC = new CalculateEBAC(SD.getSD(),BW,Wt,0);
                SoberDrinkTime = (Calendar) InitialStartDrinkTime.clone();
                SoberDrinkTime.add(Calendar.MINUTE,EBAC.getTime());
            }

            /**calculate change time by minus two times*/
            long change = ChangeStartDrinkTime.getTimeInMillis();
            long initial = InitialStartDrinkTime.getTimeInMillis();
            long diff = change - initial;
            int diff_i = (int) diff;
            SoberDrinkTime.add(Calendar.MILLISECOND,diff_i);

            Data data = DataBase.getInstance(ModifyDrink.this).getDataUao().findDataById(Id);
            data.setDrinkName(drinkName);
            data.setDrinkAbv(drinkAbv);
            data.setDrinkML(drinkML);
            data.setSD(Float.toString(SD.getSD()));
            data.setStartDrinkTime(ChangeStartDrinkTime);
            data.setSoberDrinkTime(SoberDrinkTime);
            DataBase.getInstance(ModifyDrink.this).getDataUao().updateData(data);
        }).start();

        Intent intent = new Intent(this, ViewDrinkActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void Delete(View view){
        new Thread(()->{
            DataBase.getInstance(ModifyDrink.this).getDataUao().deleteData(Id);
        }).start();

        Intent intent = new Intent(this, ViewDrinkActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    public void Cancel(View view){
        Intent intent = new Intent(this, ViewDrinkActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }




    public void changeMdDate(View v){
        datePickerDialog.show();
    }

    public void changeMdTime(View v){
        timePickerDialog.show();
    }
}
