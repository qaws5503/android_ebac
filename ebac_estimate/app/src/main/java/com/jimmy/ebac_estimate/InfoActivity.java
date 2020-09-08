package com.jimmy.ebac_estimate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_info);
        toolbar.setTitle("血液酒精濃度介紹");
        setSupportActionBar(toolbar);

        TextView textViewEBAC = findViewById(R.id.textView_selectEBAC);
        TextView textViewAction = findViewById(R.id.textView_action);
        TextView textViewDamage = findViewById(R.id.textView_damage);
        SeekBar seekBar = findViewById(R.id.seekBar_info);

        Intent intent=getIntent();
        String nowEBAC=intent.getStringExtra("EBAC");
        int nowEBAC_int = (int) (Float.parseFloat(nowEBAC)*100);
        seekBar.setProgress(nowEBAC_int);
        float nowEBAC_float = (float) nowEBAC_int/100;
        textViewEBAC.setText(String.valueOf(nowEBAC_float)+" %");
        textViewIfLoop(textViewAction,textViewDamage,nowEBAC_float);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float EBAC = (float) progress/100;
                textViewEBAC.setText(String.valueOf(EBAC)+" %");
                textViewIfLoop(textViewAction,textViewDamage,EBAC);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void textViewIfLoop(TextView textViewAction,TextView textViewDamage,float EBAC){
        if (EBAC>=0.30){
            textViewAction.setText(R.string.action_300);
            textViewDamage.setText(R.string.damage_300);
        }
        else if (EBAC>=0.20){
            textViewAction.setText(R.string.action_200);
            textViewDamage.setText(R.string.damage_200);
        }
        else if (EBAC>=0.10){
            textViewAction.setText(R.string.action_100);
            textViewDamage.setText(R.string.damage_100);
        }
        else if (EBAC>=0.060){
            textViewAction.setText(R.string.action_060);
            textViewDamage.setText(R.string.damage_060);
        }
        else if (EBAC>=0.030){
            textViewAction.setText(R.string.action_030);
            textViewDamage.setText(R.string.damage_030);
        }
        else if (EBAC!=0){
            textViewAction.setText(R.string.action_000);
            textViewDamage.setText(R.string.damage_000);
        }
        else {
            textViewAction.setText("");
            textViewDamage.setText("");
        }
    }
}
