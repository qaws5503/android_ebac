package com.jimmy.ebac_estimate.function;

import java.util.Calendar;


public class CalculateDP {
    float DP;
    public CalculateDP(Calendar startDrinkTime) {
        Calendar nowTime = Calendar.getInstance();
        startDrinkTime.set(Calendar.SECOND, 0);

        long now = nowTime.getTime().getTime();
        long drink = startDrinkTime.getTime().getTime();

        long dif = (now - drink) / (60 * 1000);
        DP = (float) dif/60;

    }
    public float getDP(){
        return DP;
    }

    public void setDP(float DP){this.DP = DP;}
}
