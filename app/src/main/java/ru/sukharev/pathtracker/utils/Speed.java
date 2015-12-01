package ru.sukharev.pathtracker.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that contains info about speed during this path
 */
public class Speed {

    private static final Double NO_SPEED = Double.valueOf(0);

    private List<Double> mSpeeds;
    private Double mCurrentSpeed;
    private Double mAverageSpeed;

    Speed() {
        mSpeeds = new ArrayList<>();
    }

    Speed(Double speed) {
        mSpeeds = new ArrayList<>();
        mSpeeds.add(speed);
        mAverageSpeed = speed;
        mCurrentSpeed = NO_SPEED;
    }

    Speed(List<Double> speeds) {
        mSpeeds = speeds;
        calculateAverage();
        mCurrentSpeed = NO_SPEED;
    }

    public void addValue(Double newSpeed) {
        mCurrentSpeed = newSpeed;
        refreshAverage(newSpeed);
        mSpeeds.add(newSpeed);
    }

    private void refreshAverage(Double newSpeed) {
        mAverageSpeed = (mAverageSpeed * mSpeeds.size() + newSpeed) / (mSpeeds.size() + 1);
    }

    private void calculateAverage() {
        Double sum = NO_SPEED;
        for (Double speed : mSpeeds)
            sum += speed;
        mAverageSpeed = sum / mSpeeds.size();
    }

    public Double getCurrentSpeed() {
        return mCurrentSpeed;
    }

    public Double getAverageSpeed() {
        return mAverageSpeed;
    }
}
