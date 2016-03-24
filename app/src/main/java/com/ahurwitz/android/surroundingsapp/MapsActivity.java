package com.ahurwitz.android.surroundingsapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

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
    float[] markerColors = {BitmapDescriptorFactory.HUE_YELLOW, BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_RED};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        Service.API API = retrofit.create(Service.API.class);
        call = API.getAllEvents();

        call.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Response<List<Event>> response) {
                try {
                    model = response.body();
                } catch (NullPointerException e) {
                    Toast toast = null;
                    if (response.code() == 401) {
                        toast = Toast.makeText(MapsActivity.this, "Unauthenticated",
                                Toast.LENGTH_SHORT);
                    } else if (response.code() >= 400) {
                        toast = Toast.makeText(MapsActivity.this, "Client Error " + response.code()
                                + " " + response.message(), Toast.LENGTH_SHORT);
                    }
                    toast.show();
                }
                mapFragment.getMapAsync(MapsActivity.this);
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

    public static TreeMap<String, Integer> sortMapByValue(TreeMap<String, Integer> districts) {
        Comparator<String> comparator = new ValueComparator(districts);
        //TreeMap is a map sorted by its keys.
        //The comparator is used to sort the TreeMap by keys.
        TreeMap<String, Integer> result = new TreeMap<String, Integer>(comparator);
        result.putAll(districts);
        return result;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
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
                Log.v(LOG_TAG, "dest: " + dest + " latlng: " + latLng.latitude + " "
                        + latLng.longitude);
                destCords.put(dest, latLng);
            }

            for (TreeMap.Entry<String, LatLng> entry : destCords.entrySet()) {
                Log.v(LOG_TAG, "Destination Cords | Districts: " + entry.getKey() + " Val: "
                        + entry.getValue());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // loop through sorted distracts and place markers according to volume of incidents
        int n = 0;
        float color = BitmapDescriptorFactory.HUE_BLUE;
        LatLngBounds.Builder sFbuilder = new LatLngBounds.Builder();

        for (TreeMap.Entry<String, Integer> entry : sortedDistricts.entrySet()) {
            /*Log.v(LOG_TAG, "Sorted Districts: K| " + entry.getKey() + " V|"
                    + entry.getValue());*/
            int disLength = sortedDistricts.size();
            int firstThird = disLength / 3;
            int secondThird = (disLength * 2)/3;

            String destName = entry.getKey();
            LatLng latLng = destCords.get(destName);
            // Create a LatLngBounds that includes Australia.
            sFbuilder.include(latLng);

            // Set the camera to the greatest possible zoom level that includes the
            // bounds
            //String color = markerColors[n];

            if(n<=firstThird){color = markerColors[0];}
            else if (n > firstThird && n <= secondThird ){color = markerColors[1];}
            else {color = markerColors[2];}

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
}





