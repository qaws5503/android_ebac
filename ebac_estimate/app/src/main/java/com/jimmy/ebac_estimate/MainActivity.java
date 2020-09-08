package com.jimmy.ebac_estimate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;



import com.ayst.dbv.DashboardView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jimmy.ebac_estimate.RoomDataBase.Data;
import com.jimmy.ebac_estimate.RoomDataBase.DataBase;
import com.jimmy.ebac_estimate.function.CalculateDP;
import com.jimmy.ebac_estimate.function.CalculateEBAC;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private FirebaseAnalytics mFirebaseAnalytics;
    private float valueEBAC;
    private AdView mAdView;
    SimpleDateFormat formatter = new SimpleDateFormat("M月d日 a hh:mm");
    SimpleDateFormat formatterToday = new SimpleDateFormat(" a hh:mm");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle("酒精計算機");
        setSupportActionBar(toolbar);

        DashboardView mDashboardView = (DashboardView) findViewById(R.id.dashboardview_1);



        TextView TextEBAC = findViewById(R.id.textView_EBAC);
        TextView textWarning = findViewById(R.id.textView_warning);
        TextView textSoberTime = findViewById(R.id.textView_soberTime);


        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(this);
        String weight = sharedPref.getString("weight","70");
        String gender = sharedPref.getString("gender","0.58");

        GlobalVariable userdata = (GlobalVariable)getApplicationContext();

        userdata.setBW(Float.parseFloat(gender));
        userdata.setWt(Float.parseFloat(weight));


        setupSharedPreferences();


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                float BW = userdata.getBW();
                float Wt = userdata.getWt();

                new Thread(() -> {
                    Calendar nowTime = Calendar.getInstance();
                    List<Data> dataList = DataBase.getInstance(MainActivity.this).getDataUao().findBetweenDrink(nowTime);
                    if (dataList!=null) {
                        valueEBAC = 0;
                        for (int i=0; i<dataList.size(); i++){
                            Data nowData = dataList.get(i);
                            String SD = nowData.getSD();
                            CalculateDP valueDP = new CalculateDP(nowData.getStartDrinkTime());
                            CalculateEBAC nowValueEBAC = new CalculateEBAC(Float.parseFloat(SD), BW, Wt, valueDP.getDP());
                            valueEBAC += nowValueEBAC.getEBAC();
                        }
                        int soberTime = (int) ((valueEBAC/0.015)*60*60);
                        nowTime.set(Calendar.SECOND,0);
                        nowTime.add(Calendar.SECOND,soberTime);
                        Calendar tomorrowTime = (Calendar) nowTime.clone();
                        tomorrowTime.add(Calendar.DATE,-1);

                        runOnUiThread(() -> {
                            TextEBAC.setText(String.format("%.3f", valueEBAC)+" %");
                            //判斷顯示顏色
                            if (valueEBAC>=0.05){
                                TextEBAC.setTextColor(Color.parseColor("#FFF44336"));
                                textWarning.setText("警告!您已構成公共危險罪");
                            }
                            else if (valueEBAC>0.03){
                                TextEBAC.setTextColor(Color.parseColor("#FFFFC107"));
                                textWarning.setText("警告!您已達酒醉駕車標準");
                            }
                            else{
                                TextEBAC.setTextColor(Color.parseColor("#FF4CAF50"));
                                textWarning.setText("");
                            }


                            if (!TextEBAC.getText().toString().equals("0.000 %")){
                                if (DateUtils.isToday(nowTime.getTimeInMillis())){
                                    textSoberTime.setText("預計於: 今日"+formatterToday.format(nowTime.getTime())+" 酒醒");
                                }
                                else if (DateUtils.isToday(tomorrowTime.getTimeInMillis())){
                                    textSoberTime.setText("預計於: 明日"+formatterToday.format(nowTime.getTime())+" 酒醒");
                                }
                                else {
                                    textSoberTime.setText("預計於: "+formatter.format(nowTime.getTime())+" 酒醒");
                                }
                            }
                            else {
                                textSoberTime.setText("");
                            }
                        });
                    }
                    else {
                        runOnUiThread(() -> {
                            valueEBAC = 0;
                            TextEBAC.setText("0.00 %");
                            TextEBAC.setTextColor(Color.parseColor("#FF29FD2F"));
                        });
                    }

                    //set dashboard value
                    int valueEBAC_int = (int) (valueEBAC*1000);//dashboard type int, so * 1000
                    mDashboardView.resetValue(valueEBAC_int);
                }).start();
            }
        },0,1000);

    }




    public void AddDrinks(View view){
        Intent intent = new Intent(this, AddDrinkActivity.class);
        startActivity(intent);
    }

    public void ViewDrinks(View view){
        Intent intent = new Intent(this, ViewDrinkActivity.class);
        startActivity(intent);
    }

    public void Introduction(View view){
        Intent intent = new Intent(this, InfoActivity.class);
        intent.putExtra("EBAC",String.valueOf(valueEBAC));
        startActivity(intent);
    }


    /**menu*/
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /**each menu option actions*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // 取得點選項目的id
        int id = item.getItemId();

        // 依照id判斷點了哪個項目並做相應事件
        if (id == R.id.action_settings) {
            // 按下「設定」要做的事
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.action_disclaimer) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("免責聲明");
            dialog.setMessage(getString(R.string.disclaimer));
            dialog.setPositiveButton("了解", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which) {}
            });
            dialog.show();
            return true;
        }
        else if (id == R.id.action_license) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("授權");
            dialog.setMessage(getString(R.string.license));
            dialog.setPositiveButton("了解", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which) {}
            });
            dialog.show();
            return true;
        }
        else if (id == R.id.action_about) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("關於");
            dialog.setMessage(getString(R.string.about_me));
            dialog.setPositiveButton("了解", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which) {}
            });
            dialog.setNeutralButton("複製email", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ClipboardManager myClipboard;
                    myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                    String text = "qaws5503@gmail.com";
                    ClipData myClip = ClipData.newPlainText("email", text);
                    myClipboard.setPrimaryClip(myClip);
                    Toast.makeText(MainActivity.this,"已複製email",Toast.LENGTH_LONG).show();
                }
            });
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**preference setup*/
    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }




    /**when setup changes change GlobalVariable*/
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        GlobalVariable userdata = (GlobalVariable)getApplicationContext();
        float Wt,BW;
        if (key.equals("weight")) {
            Wt = Float.parseFloat(sharedPreferences.getString("weight", "no text"));
            userdata.setWt(Wt);
        }
        if (key.equals("gender")) {
            BW = Float.parseFloat(sharedPreferences.getString("gender","no text"));
            userdata.setBW(BW);
        }


        /**when weight or gender change, recalculate sober time*/
        new Thread(() -> {
            List<Data> dataList = DataBase.getInstance(MainActivity.this).getDataUao().displayAll();
            if (dataList!=null) {
                for (int i=0; i<dataList.size(); i++){
                    Data nowData = dataList.get(i);
                    String SD = nowData.getSD();
                    CalculateEBAC newValueEBAC = new CalculateEBAC(Float.parseFloat(SD), userdata.getBW(), userdata.getWt(), 0);


                    int Time = newValueEBAC.getTime();
                    Calendar startTime = nowData.getStartDrinkTime();
                    Calendar soberTime = (Calendar) startTime.clone();
                    soberTime.add(Calendar.MINUTE, Time);

                    nowData.setSoberDrinkTime(soberTime);
                    DataBase.getInstance(this).getDataUao().updateData(nowData);
                }
            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        androidx.preference.PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    /** main activity 返回鍵不直接關閉*/
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("確定要離開？");
            builder.setTitle("離開");
            builder.setPositiveButton("確認", new DialogInterface.OnClickListener()  {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    MainActivity.this.finish();
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener()  {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
        }
        return false;
    }

}
