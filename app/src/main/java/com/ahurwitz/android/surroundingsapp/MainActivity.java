package com.ahurwitz.android.surroundingsapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.ahurwitz.android.surroundingsapp.model.Model;
import com.ahurwitz.android.surroundingsapp.service.Service.API;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String API_BASE_URL = "https://data.sfgov.org/resource/cuks-n6tp.json/";
    private Call<List<Model>> call;
    //private Call<Model> call;
    private List<Model> model;
    private ArrayList<Model.Event> events;
    /*private final String startDate = "2016-02-20";
    private final String endDate = "2016-03-20";*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        API API = retrofit.create(API.class);
        call = API.getAllEvents();
        //call = EventService.getParams("");
        //call = EventService.getParams(startDate, endDate);

        call.enqueue(new Callback<List<Model>>() {
            @Override
            public void onResponse(Response<List<Model>> response) {
                try {
                    model = response.body();
                    /*events = model.get(0).getEvents();
                    events.get(0).getIncidntnum();*/

                    Log.v(LOG_TAG, "Response Working | " + "Model Size: " + model.size());
                    /*Log.v(LOG_TAG, "Events: " + events.size());*/
                } catch (NullPointerException e) {
                    Toast toast = null;
                    if (response.code() == 401) {
                        toast = Toast.makeText(MainActivity.this, "Unauthenticated", Toast.LENGTH_SHORT);
                    } else if (response.code() >= 400) {
                        toast = Toast.makeText(MainActivity.this, "Client Error " + response.code()
                                + " " + response.message(), Toast.LENGTH_SHORT);
                    }
                    toast.show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("getParams threw: ", t.getMessage());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unsubscribe
        call.cancel();
    }

}

