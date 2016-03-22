package com.ahurwitz.android.surroundingsapp.service;

import com.ahurwitz.android.surroundingsapp.model.Model;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by adamhurwitz on 3/22/16.
 */
public class Service {
    public interface API {
        // build path
        // unsafe=true ensures unsafe response. Prevents HTML escape characters
        @GET("?$where=date%20between%20%272016-02-20T00:00:00%27%20and%20%272016-03-20T00:00:00%27")
        // @GET("?$where=date%20between%20%27{firstDate}T00:00:00%27%20and%20%27{secondDate}T00:00:00%27")
        // 2016-02-20
        Call<List<Model>> getAllEvents();
        /*Call<List<Model>> getParams(
                @Query("") String string);*/
            /*@Path("firstDate") String firstDate,
            @Path("secondDate") String secondDate);*/
    }
}
