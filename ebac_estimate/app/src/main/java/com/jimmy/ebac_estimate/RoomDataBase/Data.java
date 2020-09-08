package com.jimmy.ebac_estimate.RoomDataBase;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Calendar;

@Entity(tableName = "MyTable")
public class Data {


    @PrimaryKey(autoGenerate = true)//設置是否使ID自動累加
    private int id;
    private String drinkName;
    private String drinkML;
    private String drinkAbv;
    private String SD;
    private Calendar startDrinkTime;
    private Calendar soberDrinkTime;

    public Data(String drinkName, String drinkML, String drinkAbv, String SD, Calendar startDrinkTime, Calendar soberDrinkTime) {
        this.drinkName = drinkName;
        this.drinkML = drinkML;
        this.drinkAbv = drinkAbv;
        this.SD = SD;
        this.startDrinkTime = startDrinkTime;
        this.soberDrinkTime = soberDrinkTime;
    }
    @Ignore//如果要使用多形的建構子，必須加入@Ignore
    public Data(int id,String drinkName, String drinkML, String drinkAbv, String SD, Calendar startDrinkTime, Calendar soberDrinkTime) {
        this.id = id;
        this.drinkName = drinkName;
        this.drinkML = drinkML;
        this.drinkAbv = drinkAbv;
        this.SD = SD;
        this.startDrinkTime = startDrinkTime;
        this.soberDrinkTime = soberDrinkTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDrinkName() {
        return drinkName;
    }

    public void setDrinkName(String drinkName) {
        this.drinkName = drinkName;
    }

    public String getDrinkML() {
        return drinkML;
    }

    public void setDrinkML(String drinkML) {
        this.drinkML = drinkML;
    }

    public String getDrinkAbv() {
        return drinkAbv;
    }

    public void setDrinkAbv(String drinkAbv) {
        this.drinkAbv = drinkAbv;
    }

    public String getSD() {
        return SD;
    }

    public void setSD(String SD) {
        this.SD = SD;
    }
    public Calendar getStartDrinkTime() {
        return startDrinkTime;
    }

    public void setStartDrinkTime(Calendar startDrinkTime) {
        this.startDrinkTime = startDrinkTime;
    }

    public Calendar getSoberDrinkTime(){
        return soberDrinkTime;
    }

    public void setSoberDrinkTime(Calendar soberDrinkTime){
        this.soberDrinkTime = soberDrinkTime;
    }

}
