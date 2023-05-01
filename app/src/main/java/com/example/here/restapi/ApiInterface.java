package com.example.here.restapi;

import com.example.here.models.UserData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiInterface {

    @GET("/api/user/name")
    Call<Name> getName(@Header("Authorization") String authorization);

    @GET("/api/user/username")
    Call<Username> getUsername(@Header("Authorization") String authorization);

    @GET("/api/user/friends")
    Call<List<UserData>> getFriends(@Header("Authorization") String authorization);

    @POST("/api/auth")
    Call<Token> getAuthToken(@Body Credentials credentials);

    @POST("/api/user/register")
    Call<Token> register(@Body RegisterCredentials credentials);

    @POST("/api/user/add_data")
    Call<Void> addData(@Header("Authorization") String authorization, @Body UserData userData);

    @GET("/api/user/get_data")
    Call<UserData> getUserData(@Header("Authorization") String authorization);

    @POST("/api/user/edit_data")
    Call<Void> editData(@Header("Authorization") String authorization, @Body UserData userData);

    @POST("/api/user/edit_user")
    Call<Void> editName(@Header("Authorization") String authorization, @Body Name name);

}
