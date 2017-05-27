package frost.mymap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        AdapterView.OnItemSelectedListener, GoogleMap.OnMarkerClickListener {

    private static final String SIZE_PATHS = "SIZE_PATHS";
    private final String SIZE = "SIZE";
    SharedPreferences mPrefs;
    private GoogleMap mMap;
    private ArrayList<String> citiesNames;
    private ArrayList<Integer> activeCities;
    private ArrayList<Pair> paths;
    private ArrayAdapter<String> adapter;
    private CityResponse cr;
    private int first, second;
    private Boolean clicked;
    private Boolean mapReady = true;
    private Button eraseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        init();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void init() {
        mapReady = false;
        mPrefs = this.getSharedPreferences("MY_PREFS", Context.MODE_PRIVATE);
        activeCities = new ArrayList<Integer>();
        citiesNames = new ArrayList<String>();
        paths = new ArrayList<Pair>();
        eraseButton = (Button) findViewById( R.id.eraseButton );

        readJSON();
        getActiveCities();
        getPaths();
        addCickListenerToEraseButton();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        clicked = false;
        mMap = googleMap;

        addCitiesFromJsonToArray();
        createAdapter();
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        mMap.clear();
        showActiveMarkers();
        showActivePaths();

        mapReady = true;
    }

    private void readJSON() {
        String json = loadJSONFromAsset();
        cr = CityResponse.parseJSON(json);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveActiveCities();
        savePaths();
        mapReady = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapReady = true;
    }

    private void createAdapter() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, citiesNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public String loadJSONFromAsset() {
        String json = null;
        InputStream is;
        int size;
        byte[] buffer;

        try {
            is = getResources().openRawResource(
                    getResources().getIdentifier("miasta",
                            "raw", getPackageName()));
            size = is.available();
            buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        addMarker(position);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (clicked) {
            second = (int) marker.getTag();
            addLineBetween(first, second);
            Pair p = new Pair(first, second);
            if (paths.indexOf(p) == -1) {
                paths.add(new Pair(first, second));
            }
            clicked = false;
        } else {
            first = (int) marker.getTag();
            clicked = true;
        }

        return false;
    }

    private void showActiveMarkers() {
        for (int pos : activeCities) {
            addMarker(pos);
        }
    }

    private void addCitiesFromJsonToArray() {
        for (City c : cr.cities) {
            citiesNames.add(c.getCity());
        }
    }

    private void addMarker(int position) {
        LatLng curr = new LatLng(cr.cities.get(position).getLatitude(),
                cr.cities.get(position).getLongitude());

        mMap.setOnMarkerClickListener(this);
        mMap.addMarker(new MarkerOptions()
                .position(curr)
                .title(cr.cities.get(position).getCity()))
                .setTag(position);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(curr));

        if (activeCities.indexOf(position) == -1) {
            activeCities.add(position);
        }

    }

    private void addLineBetween(int first, int second) {
        mMap.addPolyline(new PolylineOptions().clickable(false).add(
                new LatLng(
                        cr.cities.get(first).getLatitude(),
                        cr.cities.get(first).getLongitude()
                ),
                new LatLng(
                        cr.cities.get(second).getLatitude(),
                        cr.cities.get(second).getLongitude()
                )
        ));
    }

    public void getActiveCities() {
        int size = mPrefs.getInt(SIZE, 0);
        int position = 0;
        Log.d("GET SIZE ", String.valueOf(size));

        for (int i = 0; i < size; i++) {
            position = mPrefs.getInt("active" + i, -1);
            if (position != -1) {
                activeCities.add(position);
            }
        }
    }

    private void saveActiveCities() {
        SharedPreferences.Editor e = mPrefs.edit();
        for (int i = 0; i < activeCities.size(); i++) {
            e.putInt("active" + i, activeCities.get(i));
        }
        e.putInt(SIZE, activeCities.size());
        e.commit();
    }

    private void savePaths() {
        SharedPreferences.Editor e = mPrefs.edit();
        e.putInt(SIZE_PATHS, paths.size());
        for (int i = 0; i < paths.size(); i++) {
            e.putInt("first" + i, paths.get(i).first);
            e.putInt("second" + i, paths.get(i).second);
        }
        e.commit();
    }

    private void showActivePaths() {
        for (Pair p : paths) {
            addLineBetween( p.first, p.second );
        }
    }

    public void getPaths() {
        int size = mPrefs.getInt(SIZE_PATHS, 0 );
        int first;
        int second;

        for( int i = 0 ; i < size ; i++ ){
            first = mPrefs.getInt( "first" + i, 0);
            second = mPrefs.getInt( "second" + i, 0);
            paths.add( new Pair( first, second ) );
        }
    }

    private void addCickListenerToEraseButton() {
        eraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( mapReady ){
                    paths = new ArrayList<Pair>();
                    activeCities = new ArrayList<Integer>();
                    mMap.clear();
                }
            }
        });
    }

}
