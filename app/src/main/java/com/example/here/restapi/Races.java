package com.example.here.restapi;

import java.util.ArrayList;

public class Races {
    ArrayList<races> races;

    public ArrayList<races> getRacess() {
        return races;
    }

    public void setRaces(ArrayList<races> races) {
        this.races = races;
    }

    public class races{
        Integer race_id;
        String name;
        String date_time;
        String city;
        Integer limit_of_participants;
        Integer route_id;
        Integer organizer_id;
        Boolean visibility;

        public Integer getRace_id() {
            return race_id;
        }

        public void setRace_id(Integer race_id) {
            this.race_id = race_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDate_time() {
            return date_time;
        }

        public void setDate_time(String date_time) {
            this.date_time = date_time;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public Integer getLimit_of_participants() {
            return limit_of_participants;
        }

        public void setLimit_of_participants(Integer limit_of_participants) {
            this.limit_of_participants = limit_of_participants;
        }

        public Integer getRoute_id() {
            return route_id;
        }

        public void setRoute_id(Integer route_id) {
            this.route_id = route_id;
        }

        public Integer getOrganizer_id() {
            return organizer_id;
        }

        public void setOrganizer_id(Integer organizer_id) {
            this.organizer_id = organizer_id;
        }

        public Boolean getVisibility() {
            return visibility;
        }

        public void setVisibility(Boolean visibility) {
            this.visibility = visibility;
        }
    }


}
