package com.example.here.models;

import com.example.here.restapi.Coordinates;
import com.google.gson.annotations.SerializedName;

public class Route {
    @SerializedName("route_id")
    private Integer route_id;
    @SerializedName("coordinates")
    private Coordinates coordinates;

    public Route() {

    }

    public Route(Integer route_id, Coordinates coordinates) {
        this.route_id = route_id;
        this.coordinates = coordinates;
    }

    public Integer getRoute_id() {
        return route_id;
    }

    public void setRoute_id(Integer route_id) {
        this.route_id = route_id;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }
}
