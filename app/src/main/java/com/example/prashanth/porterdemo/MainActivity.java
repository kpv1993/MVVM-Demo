package com.example.prashanth.porterdemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prashanth.porterdemo.viewmodel.MainViewModel;
import com.example.prashanth.porterdemo.viewmodel.ViewModelFactory;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static String TAG = "MainActivity";

    @BindView(R.id.from)
    TextView from;
    @BindView(R.id.destination)
    TextView destination;
    @BindView(R.id.costEtaRL)
    RelativeLayout costEtaRL;
    @BindView(R.id.cost)
    TextView costText;
    @BindView(R.id.eta)
    TextView eta;
    @BindView(R.id.pBar)
    ProgressBar pBar;
    @BindView(R.id.background)
    View background;
    @BindView(R.id.accountBlock)
    TextView accountBlock;

    private static int REQUEST_CODE_AUTOCOMPLETE = 99;
    double lat,lng;
    SupportMapFragment mapFragment;
    MainViewModel viewModel;
    private static String WAIT = "wait";
    private static String SUCCESS = "success";
    private static String FAIL = "fail";
    String costState = WAIT, etaState = WAIT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        //Standard lines for architecture components
        ViewModelFactory factory = ViewModelFactory.getInstance();
        viewModel = ViewModelProviders.of(this, factory).get(MainViewModel.class);
        getLifecycle().addObserver(viewModel);

        viewModel.getServiceObservable().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if(aBoolean == null){
                    return;
                }

                if(aBoolean){
                    accountBlock.setVisibility(View.GONE);
                } else {
                    accountBlock.setVisibility(View.VISIBLE);
                }
            }
        });

        viewModel.getCostObservable().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer == null){
                    return;
                }
                Log.d(TAG, "getCostObservable()");

                costState = SUCCESS;
                costText.setText("Rs " + integer);
                if(SUCCESS.equals(etaState)) {
                    Log.d(TAG, "successCost");
                    pBar.setVisibility(View.GONE);
                    background.setVisibility(View.GONE);
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            LatLng marker = new LatLng(lat, lng);
                            googleMap.addMarker(new MarkerOptions().position(marker)
                                    .title("Location").icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromView(costEtaRL))));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 14.0f));

                        }
                    });
                } else if (FAIL.equals(etaState)){
                    pBar.setVisibility(View.GONE);
                    background.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Sorry Something went wrong. Please try again", Toast.LENGTH_LONG);
                } else if(WAIT.equals(etaState)){

                }

            }
        });

        viewModel.getEtaObservable().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer integer) {
                if(integer == null){
                    return;
                }
                Log.d(TAG, "getEtaObservable()");
                etaState = SUCCESS;
                eta.setText(integer+" Mins");
                if(SUCCESS.equals(costState)) {
                    Log.d(TAG, "successEta");
                    pBar.setVisibility(View.GONE);
                    background.setVisibility(View.GONE);
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            LatLng marker = new LatLng(lat, lng);
                            googleMap.addMarker(new MarkerOptions().position(marker)
                                    .title("Location").icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromView(costEtaRL))));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 14.0f));

                        }
                    });
                } else if (FAIL.equals(costState)){
                    pBar.setVisibility(View.GONE);
                    background.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Sorry Something went wrong. Please try again", Toast.LENGTH_LONG);
                } else if(WAIT.equals(costState)){

                }
            }
        });
        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAutocompleteActivity();
            }
        });

        destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAutocompleteActivity();
            }
        });

        alarmCheckForServiceability();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "OnStart");
        costState = WAIT;
        etaState = WAIT;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "OnStop");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if(!isNetworkAvailable(MainActivity.this)){
            Toast.makeText(MainActivity.this, "Network Not Available", Toast.LENGTH_LONG).show();
        }
    }

    private void openAutocompleteActivity() {
        try {
            Log.e(TAG, "openAutocompleteActivity");
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(this);
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            Log.e(TAG, "GooglePlayServicesRepairableException");
            GoogleApiAvailability.getInstance().getErrorDialog(this, e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, "GooglePlayServicesNotAvailableException");
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Log.e(TAG, message);
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place Selected: " + place.getName());
                from.setText(place.getAddress());

                lat = place.getLatLng().latitude;
                lng = place.getLatLng().longitude;

                pBar.setVisibility(View.VISIBLE);
                background.setVisibility(View.VISIBLE);
                viewModel.getCost(lat, lng);
                viewModel.getEta(lat, lng);

                // Display attributions if required.
                CharSequence attributions = place.getAttributions();
//                if (!TextUtils.isEmpty(attributions)) {
//                    mPlaceAttribution.setText(Html.fromHtml(attributions.toString()));
//                } else {
//                    mPlaceAttribution.setText("");
//                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e(TAG, "Error: Status = " + status.toString());
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }


    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    private void alarmCheckForServiceability(){
        Context ctx = getApplicationContext();
        Calendar cal = Calendar.getInstance();
        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        long interval = 1000 * 20; // 20 seconds
        Intent serviceIntent = new Intent(ctx, CheckServiceabilityService.class);
        PendingIntent servicePendingIntent = PendingIntent.getService(ctx,
                        CheckServiceabilityService.SERVICE_ID,
                        serviceIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT);
        am.setRepeating( AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), interval, servicePendingIntent);
    }

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

}
