package com.ahurwitz.android.surroundingsapp.service;

import com.ahurwitz.android.surroundingsapp.model.Event;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by adamhurwitz on 3/22/16.
 */
public class Service {
    public interface API {
        // build path
        @GET("?$where=date%20between%20%272015-12-1T00:00:00%27%20and%20%272015-12-31T00:00:00%27")
        Call<List<Event>> getAllEvents();
    }
}
