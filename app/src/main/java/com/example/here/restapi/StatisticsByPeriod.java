package com.example.here.restapi;

public class StatisticsByPeriod {

    Float distance;
    Float calories;
    Float duration;
    Float speed;
    Integer count;

    @Override
    public String toString() {
        return "StatisticsByPeriod{" +
                "distance=" + distance +
                ", calories=" + calories +
                ", duration=" + duration +
                ", speed=" + speed +
                ", count=" + count +
                '}';
    }

    public StatisticsByPeriod() {}

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

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
