package com.ahurwitz.android.surroundingsapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.ahurwitz.android.surroundingsapp.model.Event;
import com.ahurwitz.android.surroundingsapp.service.Service.API;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String API_BASE_URL = "https://data.sfgov.org/resource/cuks-n6tp.json/";
    private Call<List<Event>> call;
    //private Call<Event> call;
    private List<Event> model;
    private ArrayList<Event> events;
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

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Response<List<Event>> response) {
                try {
                    model = response.body();

                    // initialize HashMap to store counts of district events
                    HashMap<String,Integer> districts = new HashMap<String, Integer>();
                    for (Event e : model) {
                        Integer count = districts.get(e.getPddistrict());
                        if (count == null){
                            districts.put(e.getPddistrict(), 1);
                        }
                        else {
                            districts.put(e.getPddistrict(),count+1);
                        }
                    }

                    for(HashMap.Entry<String, Integer> entry : districts.entrySet()){
                        Log.v(LOG_TAG, "Districts: K| " + entry.getKey() + " V|" + entry.getValue());
                    }

                    // use comparator to convert HashMap into TreeMap with sorted Values
                    TreeMap<String, Integer> sortedDistricts = sortMapByValue(districts);
                    System.out.println(sortedDistricts);

                    for (TreeMap.Entry<String, Integer> entry : sortedDistricts.entrySet()){
                        Log.v(LOG_TAG, "Sorted Districts: K| " + entry.getKey() + " V|" + entry.getValue());
                    }


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

    public static TreeMap<String, Integer> sortMapByValue(HashMap<String, Integer> districts){
        Comparator<String> comparator = new ValueComparator(districts);
        //TreeMap is a map sorted by its keys.
        //The comparator is used to sort the TreeMap by keys.
        TreeMap<String, Integer> result = new TreeMap<String, Integer>(comparator);
        result.putAll(districts);
        return result;
    }
}

// a comparator that compares Strings
class ValueComparator implements Comparator<String>{

    HashMap<String, Integer> map = new HashMap<String, Integer>();

    public ValueComparator(HashMap<String, Integer> map){
        this.map.putAll(map);
    }

    @Override
    public int compare(String s1, String s2) {
        if(map.get(s1) <= map.get(s2)){
            return -1;
        }else{
            return 1;
        }
    }
}

