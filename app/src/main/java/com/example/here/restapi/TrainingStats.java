package com.example.here.restapi;

public class TrainingStats {

    Float distance;
    Float calories;
    Float duration;

    public TrainingStats(Float distance, Float calories, Float duration) {
        this.distance = distance;
        this.calories = calories;
        this.duration = duration;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Float getCalories() {
        return calories;
    }

    public void setCalories(Float calories) {
        this.calories = calories;
    }

    public Float getDuration() {
        return duration;
    }

    public void setDuration(Float duration) {
        this.duration = duration;
    }

    public boolean isEmpty() {
        return (distance == null & duration == null & calories == null);
    }
}
