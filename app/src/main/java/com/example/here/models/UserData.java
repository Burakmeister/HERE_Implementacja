package com.example.here.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserData {
    @SerializedName("id")
    private Integer id;
    @SerializedName("sex")
    private Character sex;
    @SerializedName("height")
    private Integer height;
    @SerializedName("weigth")
    private Float weight;
    @SerializedName("country")
    private String country;
    @SerializedName("avatar")
    private String avatar;
    @SerializedName("nick")
    private String nick;
    @SerializedName("age")
    private Integer age;
    @SerializedName("language")
    private String language;
    @SerializedName("user")
    private Integer user;
    @SerializedName("friends")
    private List<Object> friends;
    @SerializedName("races")
    private List<Object> races;
    @SerializedName("birth_date")
    private String birthDate;


    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public UserData() {

    }
    public UserData(Integer id, Character sex, Integer height, Float weight, String country, String avatar, String nick, Integer age, String language, Integer user, List<Object> friends, List<Object> races, String birthDate) {
        this.id = id;
        this.sex = sex;
        this.height = height;
        this.weight = weight;
        this.country = country;
        this.avatar = avatar;
        this.nick = nick;
        this.age = age;
        this.language = language;
        this.user = user;
        this.friends = friends;
        this.races = races;
        this.birthDate = birthDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Character getSex() {
        return sex;
    }

    public void setSex(Character sex) {
        this.sex = sex;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Float getWeight() {
        return weight;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public List<Object> getFriends() {
        return friends;
    }

    public void setFriends(List<Object> friends) {
        this.friends = friends;
    }

    public List<Object> getRaces() {
        return races;
    }

    public void setRaces(List<Object> races) {
        this.races = races;
    }
}

