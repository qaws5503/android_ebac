package com.jimmy.ebac_estimate;

import android.app.Application;

public class GlobalVariable extends Application {
    private float BW,Wt,DP;

    //修改 變數値
    public void setBW(float BW){
        this.BW = BW;
    }
    public void setWt(float Wt){
        this.Wt = Wt;
    }


    //取得 變數值
    public float getBW() {
        return BW;
    }
    public float getWt(){
        return Wt;
    }

}