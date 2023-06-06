package com.example.here.home;

public class FriendsStatus {
    private String nickname;
    private Float distance = null;
    private Integer position = null;
    private String race;


    public void setRace(String race) {
        this.race = race;
    }

    public FriendsStatus() {}

    public FriendsStatus(String nickname, Float distance, Integer position, String race) {
        this.nickname = nickname;
        this.distance = distance;
        this.position = position;
        this.race = race;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getRace() {
        return this.race;
    }
}
