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
        @GET("?$where=date%20between%20%272016-02-20T00:00:00%27%20and%20%272016-03-20T00:00:00%27")
        // 2016-02-20
        Call<List<Model.Event>> getAllEvents();
    }
}
