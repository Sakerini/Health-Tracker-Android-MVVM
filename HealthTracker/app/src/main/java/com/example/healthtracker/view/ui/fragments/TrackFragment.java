package com.example.healthtracker.view.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.navigation.Navigation;

import com.example.healthtracker.R;
import com.example.healthtracker.databinding.FragmentTrackBinding;
import com.example.healthtracker.model.User;
import com.example.healthtracker.service.htppservices.HealthTrackerHttpServices;
import com.example.healthtracker.view.ui.MainActivity;

public class TrackFragment extends Fragment {

    private static final String TAG = "TrackFragment";
    private FragmentTrackBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_track, container, false);
        binding.tempFall.setVisibility(View.GONE);

        User.getInstance().getFallen().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean fallState) {
                if (fallState == true) {
                    Log.d(TAG, "onChanged: FALL_HAPPENED");
                    Navigation.findNavController(getView()).navigate(R.id.action_trackFragment_to_fallFragment);
                }
            }
        });

        binding.ibSosBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //todo Analyze User Data Exchanges between activities
                Navigation.findNavController(view).navigate(R.id.action_trackFragment_to_alertFragment);
                return false;
            }
        });
        binding.btnStopTracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo Analyze User Data Exchanges between activities
                ((MainActivity) getActivity()).stopTracking();
                ((MainActivity) getActivity()).unscheduleSendSensorsData();

                HealthTrackerHttpServices healthTrackerHttpServices = new HealthTrackerHttpServices();
                String jsonResponseBody = null;
                try {
                    jsonResponseBody = healthTrackerHttpServices.getRequest("https://lg.perf.group/controllers/"
                            + User.getInstance().getDeviceId().getValue());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (jsonResponseBody != null && (jsonResponseBody.contains("true"))) {
                    HealthTrackerHttpServices httpServices = new HealthTrackerHttpServices();
                    String json = "{}";
                    String url = "https://lg.perf.group/controllers/" + User.getInstance().getDeviceId().getValue();
                    httpServices.putRequest(url, json);// HERE IT SHOULD CALL A PUT REQUEST BECAUSE WE
                    // CHANGED STATUS SO THAT IT CHANGES ON SERVER TOO
                }
                User.getInstance().setConnected(false);

                Navigation.findNavController(view).navigate(R.id.action_trackFragment_to_startActivity);
            }
        });
        binding.tempFall.setOnClickListener(new View.OnClickListener() { // TO BE REMOVED JUST FOR FALL TEST PURPOSES
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(R.id.action_trackFragment_to_fallFragment);
            }
        });
        return binding.getRoot();
    }
}