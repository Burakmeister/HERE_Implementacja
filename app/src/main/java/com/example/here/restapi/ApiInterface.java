package com.example.here.restapi;

import com.example.here.models.UserData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("/api_users")
    Call<List<UserData>> getAllUsersData();

}
