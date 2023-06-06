package com.example.here.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Race {
    @SerializedName("race_id")
    private Integer race_id;
    @SerializedName("date_time")
    private Date date;  // czy na pewno Date??
    @SerializedName("city")
    private String city;
    @SerializedName("limit_of_participants")
    private Integer limit;
    @SerializedName("name")
    private String name;
    @SerializedName("route")
    private Route route;
    @SerializedName("organizer")
    private UserData organizer;
    @SerializedName("participant")
    private List<UserData> participants;
    @SerializedName("visibility")
    private Boolean visibility;

    public Race() {

    }

    public Race(Integer id, Date date, String city, Route route, UserData organizer, List<UserData> participants, Boolean visibility) {
        this.race_id = id;
        this.date = date;
        this.city = city;
        this.limit = limit;
        this.route = route;
        this.organizer = organizer;
        this.participants = participants;
        this.visibility = visibility;
    }

    public Integer getRaceId() {
        return race_id;
    }

    public void setRaceId(Integer raceId) {
        this.race_id = raceId;
    }

    public Date getDateTime() {
        return date;
    }

    public void setDateTime(Date dateTime) {
        this.date = dateTime;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getLimitOfParticipants() {
        return limit;
    }

    public void setLimitOfParticipants(Integer limitOfParticipants) {
        this.limit = limitOfParticipants;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public UserData getOrganizer() {
        return organizer;
    }

    public void setOrganizer(UserData organizer) {
        this.organizer = organizer;
    }

    public List<UserData> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserData> participants) {
        this.participants = participants;
    }

    public Boolean getVisibility() {
        return visibility;
    }

    public void setVisibility(Boolean visibility) {
        this.visibility = visibility;
    }

}
