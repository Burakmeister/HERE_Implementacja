package com.example.here.restapi;

import com.example.here.PersonFragment;
import com.example.here.models.UserData;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiInterface {

//    @GET("/api/users_data")
//    Call<List<UserData>> getAllUsersData();

    @GET("/api/user/firstname")
    Call<Firstname> getFirstname(@Header("Authorization") String authorization);

    @GET("/api/user/friends")
    Call<List<UserData>> getFriends(@Header("Authorization") String authorization);

    @POST("/api/auth")
    Call<Token> getAuthToken(@Body Credentials credentials);
}
