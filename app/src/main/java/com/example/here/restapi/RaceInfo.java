package com.example.here.restapi;

import com.google.gson.annotations.SerializedName;

public class RaceInfo {

    Integer race_id;
     String name;
     String date_time;
     String city;
     Integer limit_of_participants;
    Integer route_id;
    Integer organizer_id;
     Boolean visibility;

    public RaceInfo(Integer race_id, String name, String date_time, String city, Integer limit_of_participants, Integer route_id, Integer organizer_id, Boolean visibility) {
        this.race_id = race_id;
        this.name = name;
        this.date_time = date_time;
        this.city = city;
        this.limit_of_participants = limit_of_participants;
        this.route_id = route_id;
        this.organizer_id = organizer_id;
        this.visibility = visibility;
    }


    public String getRaceName() {
        return name;
    }

    public String getCity() {
        return city;
    }
}
