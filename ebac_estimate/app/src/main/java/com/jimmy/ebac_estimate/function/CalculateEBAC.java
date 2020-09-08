package com.jimmy.ebac_estimate.function;

public class CalculateEBAC {
    private float MR = (float) 0.015;
    private float EBAC = 0;
    private int Time;
    public CalculateEBAC(float SD,float BW,float Wt,float DP){
        EBAC = (float) ((0.806*SD*1.2)/(BW*Wt)-(MR*DP));
        if (EBAC<0){
            EBAC = 0;
        }

        Time = (int) ((0.806*SD*1.2*60)/(BW*Wt*MR));//minute
    }
    public float getEBAC(){return EBAC;}

    public void setEBAC(float EBAC){this.EBAC = EBAC;}

    public int getTime(){return Time;}
}
