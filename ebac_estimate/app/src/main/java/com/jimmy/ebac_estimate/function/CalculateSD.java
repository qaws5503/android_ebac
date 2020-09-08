package com.jimmy.ebac_estimate.function;

public class CalculateSD {
    public float SD;
    public CalculateSD(float drink,float abv) {
        SD = (float) (drink * abv * 0.789 / 1000);
    }

    public float getSD() {
        return SD;
    }
}
