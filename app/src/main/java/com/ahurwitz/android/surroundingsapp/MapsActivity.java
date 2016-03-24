package com.ahurwitz.android.surroundingsapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ahurwitz.android.surroundingsapp.model.Event;
import com.ahurwitz.android.surroundingsapp.service.Service;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private final String LOG_TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private static final String API_BASE_URL = "https://data.sfgov.org/resource/cuks-n6tp.json/";
    private Call<List<Event>> call;
    private List<Event> model;
    SupportMapFragment mapFragment;

    // array of marker colors for google map
    float[] markerColors = {BitmapDescriptorFactory.HUE_YELLOW, BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_RED};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // create retrofit builder with base url
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // create api call adding url path
        Service.API API = retrofit.create(Service.API.class);
        call = API.getAllEvents();

        // make api call
        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Response<List<Event>> response) {
                try {
                    // save list of events to instance variable to use in google maps onMapReady()
                    // method
                    model = response.body();
                } catch (NullPointerException e) {
                    if (response.code() == 401) {
                        Log.v(LOG_TAG, "Unauthenticated");
                    } else if (response.code() >= 400) {
                        Log.v(LOG_TAG, "Client Error " + response.code() + " "
                                + response.message());
                    }
                    ;
                }
                mapFragment.getMapAsync(MapsActivity.this);
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("getParams threw: ", t.getMessage());
            }
        });
    }

    // stop call to API server
    @Override
    protected void onStop() {
        super.onStop();
        // Unsubscribe
        call.cancel();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // initialize HashMap to store counts of district events
        TreeMap<String, Integer> districts = new TreeMap<String, Integer>();
        for (Event e : model) {
            // null doesn't crash the app
            Integer count = districts.get(e.getPddistrict());
            if (count == null) {
                districts.put(e.getPddistrict(), 1);
            } else {
                districts.put(e.getPddistrict(), count + 1);
            }
        }

        // sort districts by event volume
        TreeMap<String, Integer> sortedDistricts = sortMapByValue(districts);


        // parse through JSON file to get district destination coordinates

        // get values of destination coordinates and store in HashMap
        HashMap<String, LatLng> destCords = new HashMap<>();

        String json = null;
        try {
            InputStream is = getAssets().open("districts.geojson");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
            JSONObject destCordObj = new JSONObject(json);
            JSONArray jsonArray = destCordObj.getJSONArray("features");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject containerObj = jsonArray.getJSONObject(i);
                JSONObject propObj = containerObj.getJSONObject("properties");
                String dest = propObj.getString("district");

                JSONObject geoObj = containerObj.getJSONObject("geometry");
                JSONArray coordArray = geoObj.getJSONArray("coordinates");
                LatLng latLng = new LatLng(coordArray.getDouble(1), coordArray.getDouble(0));
                destCords.put(dest, latLng);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // loop through sorted districts and place markers according to volume of incidents
        int n = 0;
        float color = BitmapDescriptorFactory.HUE_BLUE;
        LatLngBounds.Builder sFbuilder = new LatLngBounds.Builder();

        for (TreeMap.Entry<String, Integer> entry : sortedDistricts.entrySet()) {
            int disLength = sortedDistricts.size();
            int firstThird = disLength / 3;
            int secondThird = (disLength * 2) / 3;

            String destName = entry.getKey();
            LatLng latLng = destCords.get(destName);
            sFbuilder.include(latLng);


            if (n <= firstThird) {
                color = markerColors[0];
            } else if (n > firstThird && n <= secondThird) {
                color = markerColors[1];
            } else {
                color = markerColors[2];
            }

            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title(destName)
                    .icon(BitmapDescriptorFactory.defaultMarker(color)));
            n++;
        }

        LatLngBounds bounds = sFbuilder.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }


    // a comparator that compares Strings
    static class ValueComparator implements Comparator<String> {

        TreeMap<String, Integer> map = new TreeMap<>();

        public ValueComparator(TreeMap<String, Integer> map) {
            this.map.putAll(map);
        }

        @Override
        public int compare(String s1, String s2) {
            if (map.get(s1) <= map.get(s2)) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    /**
     * @param districts passes in sorted districts by key
     * @return TreeMap of sorted districts by value
     */
    public static TreeMap<String, Integer> sortMapByValue(TreeMap<String, Integer> districts) {
        Comparator<String> comparator = new ValueComparator(districts);
        //TreeMap is a map sorted by its keys.
        //The comparator is used to sort the TreeMap by keys.
        TreeMap<String, Integer> result = new TreeMap<String, Integer>(comparator);
        result.putAll(districts);
        return result;
    }
}





