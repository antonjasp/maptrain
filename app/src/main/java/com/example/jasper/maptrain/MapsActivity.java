package com.example.jasper.maptrain;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DrawerLayout drawerLayout;
    TextView Azimut;


    TextView shyrotaspg;
    TextView shyrotaspm;
    TextView shyrotasps;

    TextView dovgotaspg;
    TextView dovgotaspm;
    TextView dovgotasps;
    TextView result;
    Button Viznachit;
    TextView Popravka;// поправка при переході з д кута в м азімут
    TextView Vysota;// висота
    TextView Vydstan;// відстань
    TextView Convert;
    NavigationView navigationView;



    private LocationManager locationManager;

    //---------------------------------LocationListener-------------------------------------------------------
    //формула для перевода координат з формату ддд.ддддд в дд мм сс
    public static String doubleToDegree(double value) {
        int degree = (int) value;
        double rawMinute = Math.abs((value % 1) * 60);
        return String.format("%d", degree);
    }

    public static String doubleToMinute(double value) {
        int degree = (int) value;
        double rawMinute = Math.abs((value % 1) * 60);
        int minute = (int) rawMinute;

        return String.format("%d", minute);
    }

    public static String doubleToSecond(double value) {

        double rawMinute = Math.abs((value % 1) * 60);
        double p = ((rawMinute % 1) * 60) * 100;
        double second = Math.round(p);
        second /= 100;
        return Double.toString(second);
    }

    //формула для перевода координат з формату ддд.ддддд в дд мм сс
    //формула для перевода координат з формату дд мм сс в ддд.ддддд
    public static String doubleToConvCordinates(double gg, double min, double sec) {

        double ddd;
        double dd = gg;
        double mm = min;
        double ss = sec;
        ddd = dd + (mm / 60) + (ss / 3600);
        return Double.toString(ddd);
    }

    //формула для перевода координат з формату дд мм сс в ддд.ддддд
    private LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Toast.makeText(MapsActivity.this, "Кординати оновлено!", Toast.LENGTH_SHORT).show();
            double a = location.getLatitude();
            double b = location.getLongitude();
            // перевод в  градуси минути секунди и запись в соответствующие view
            shyrotaspg.setText(String.valueOf(doubleToDegree(a)));
            shyrotaspm.setText(String.valueOf(doubleToMinute(a)));
            shyrotasps.setText(String.valueOf(doubleToSecond(a)));

            dovgotaspg.setText(String.valueOf(doubleToDegree(b)));
            dovgotaspm.setText(String.valueOf(doubleToMinute(b)));
            dovgotasps.setText(String.valueOf(doubleToSecond(b)));
            // перевод в  градуси минути секунди и запись в соответствующие view
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
//            dovgota.setText(provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
//            dovgota.setText(provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
//            dovgota.setText(provider);
        }
    };
    //---------------------------------LocationListener-------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        initToolbar();
        initDrawer();
        chechPermissions();
        initCompass();

        navigationView = findViewById(R.id.nav_view);

        View parentView = navigationView.getHeaderView(0);

        drawerLayout = findViewById(R.id.drawer_layout);
        shyrotaspg = parentView.findViewById(R.id.editText);
        shyrotaspm = parentView.findViewById(R.id.editText2);
        shyrotasps = parentView.findViewById(R.id.editText3);

        dovgotaspg = parentView.findViewById(R.id.editText4);
        dovgotaspm = parentView.findViewById(R.id.editText5);
        dovgotasps = parentView.findViewById(R.id.editText6);

        result = parentView.findViewById(R.id.editText7);//  результат
        Popravka = parentView.findViewById(R.id.editText10);
        Vysota = parentView.findViewById(R.id.editText11);
        Vydstan = parentView.findViewById(R.id.editText8);
        Azimut = parentView.findViewById(R.id.editText9);
        Convert = parentView.findViewById(R.id.editText12);

        Viznachit = parentView.findViewById(R.id.button1);


        //Viznachit = navigationView.findViewById(R.id.button1);

       /* drawerLayout = findViewById(R.id.drawer_layout);
        shyrotaspg = findViewById(R.id.editText);
        shyrotaspm = findViewById(R.id.editText2);
        shyrotasps = findViewById(R.id.editText3);

        dovgotaspg = findViewById(R.id.editText4);
        dovgotaspm = findViewById(R.id.editText5);
        dovgotasps = findViewById(R.id.editText6);

        result = findViewById(R.id.editText7);//  результат
        Popravka = findViewById(R.id.editText10);
        Vysota = findViewById(R.id.editText11);
        Vydstan = findViewById(R.id.editText8);
        Azimut = findViewById(R.id.editText9);
        Convert = findViewById(R.id.editText12);

        Viznachit = findViewById(R.id.button1);*/


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        // GoogleMap.setBuildingsEnabled(true);
        //moveCamera();


        Viznachit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Toast toast = Toast.makeText(getApplicationContext(),
                        " текст!", Toast.LENGTH_SHORT);
                toast.show();
                }
        });
    }




    //---------------------------------azimyt-------------------------------------------------------
    private void initCompass(){
        SensorManager mSensorManager;
        mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mSensorManager.registerListener(new SensorEventListener() {
                                                @Override
                                                public void onSensorChanged(SensorEvent event) {
                                                    Azimut.setText(String.valueOf(Math.round(event.values[0])));
                                                }
                                                @Override
                                                public void onAccuracyChanged(Sensor sensor, int accuracy) {

                                                }},
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),48, SensorManager.SENSOR_DELAY_GAME);
        }

    }
    //---------------------------------azimyt-------------------------------------------------------
    //---------------------------------Location-------------------------------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1, 1, mLocationListener);
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1, 1,
                mLocationListener);
    }

    private void chechPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


//                } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
//                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }//---------------------------------Location-------------------------------------------------------
    //--------------------------------------------------------------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!






        @Override
        public void onMapReady (GoogleMap googleMap){
            mMap = googleMap;

            LatLng Kyiv = new LatLng(50.43111111111111, 30.54333333333337);
            mMap.addMarker(new MarkerOptions().position(Kyiv).title("Marker in Sydney"));


            mMap.moveCamera(CameraUpdateFactory.newLatLng(Kyiv));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        public void initToolbar() {
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            ActionBar actionbar = getSupportActionBar();
            actionbar.setTitle("IJK1");
            actionbar.setDisplayHomeAsUpEnabled(true);
            actionbar.setHomeAsUpIndicator(R.drawable.common_full_open_on_phone);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item){
            switch (item.getItemId()) {
                case android.R.id.home:
                    drawerLayout.openDrawer(GravityCompat.START);
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }

        public void initDrawer() {
            drawerLayout = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(
                    new NavigationView.OnNavigationItemSelectedListener() {
                        @Override
                        public boolean onNavigationItemSelected(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.nav_camera:
                                    Toast.makeText(MapsActivity.this, "camera", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.nav_add:
                                    LatLng Klk = new LatLng(50.4306779, 30.5431710);
                                    mMap.addMarker(new MarkerOptions().position(Klk).title("Marker in Sydney"));
                                    break;
                            }
                            // set item as selected to persist highlight
                            menuItem.setChecked(true);
                            // close drawer when item is tapped
                            drawerLayout.closeDrawers();

                            // Add code here to update the UI based on the item selected
                            // For example, swap UI fragments here

                            return true;
                        }
                    });
        }




}