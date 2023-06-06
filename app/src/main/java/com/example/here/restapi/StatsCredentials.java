package com.example.here.restapi;

import java.util.Date;

public class StatsCredentials {
    Integer position;
    Float distance;
    Date date;
    Integer burned_calories;
    Integer duration;
    Long race_id;

    public StatsCredentials(Integer position, Float distance, Date date, Integer burned_calories, Integer duration, Long race_id) {
        this.position = position;
        this.distance = distance;
        this.date = date;
        this.burned_calories = burned_calories;
        this.duration = duration;
        this.race_id = race_id;
    }
    public StatsCredentials(Integer position, Float distance, Date date, Integer burned_calories, Integer duration) {
        this.position = position;
        this.distance = distance;
        this.date = date;
        this.burned_calories = burned_calories;
        this.duration = duration;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getBurned_calories() {
        return burned_calories;
    }

    public void setBurned_calories(Integer burned_calories) {
        this.burned_calories = burned_calories;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Long getRace_id() {
        return race_id;
    }

    public void setRace_id(Long race_id) {
        this.race_id = race_id;
    }
}
