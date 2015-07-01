package com.diploma.android.iruntracking.fragments;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.diploma.android.iruntracking.R;
import com.diploma.android.iruntracking.activities.ChartsActivity;
import com.diploma.android.iruntracking.activities.RecordsActivity;
import com.diploma.android.iruntracking.activities.RunActivity;
import com.diploma.android.iruntracking.activities.RunListActivity;
import com.diploma.android.iruntracking.activities.SettingsActivity;

public class MainFragment extends Fragment implements LocationListener {
    private static final int REQUEST_GPS = 0;

    private Button mStartRunButton;

    private LocationManager mLocationManager;

    private View.OnClickListener mEnableGPSClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(callGPSSettingIntent, REQUEST_GPS);
        }
    };

    private View.OnClickListener mStartRunClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(getActivity(), RunActivity.class);
            startActivity(i);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mStartRunButton = (Button) view.findViewById(R.id.start_run_button);
        Button viewRunsButton = (Button) view.findViewById(R.id.view_runs_button);
        Button viewRecordsButton = (Button) view.findViewById(R.id.view_records_button);
        Button viewChartsButton = (Button) view.findViewById(R.id.view_charts_button);

        if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            onProviderEnabled(LocationManager.GPS_PROVIDER);
        } else {
            onProviderDisabled(LocationManager.GPS_PROVIDER);
        }

        viewChartsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ChartsActivity.class);
                startActivity(i);
            }
        });

        viewRunsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), RunListActivity.class);
                startActivity(i);
            }
        });

        viewRecordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), RecordsActivity.class);
                startActivity(i);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.run_list_options, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_settings:
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        mStartRunButton.setText(getActivity().getString(R.string.new_run));
        mStartRunButton.setOnClickListener(mStartRunClickListener);
    }

    @Override
    public void onProviderDisabled(String provider) {
        mStartRunButton.setText(getActivity().getString(R.string.enable_gps));
        mStartRunButton.setOnClickListener(mEnableGPSClickListener);
    }
}
