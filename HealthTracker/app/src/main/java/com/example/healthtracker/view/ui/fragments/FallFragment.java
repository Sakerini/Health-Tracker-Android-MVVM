package com.example.healthtracker.view.ui.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.healthtracker.R;
import com.example.healthtracker.databinding.FragmentFallBinding;
import com.example.healthtracker.model.User;
import com.example.healthtracker.service.viewmodels.MainViewModel;


public class FallFragment extends Fragment {

    private FragmentFallBinding binding;
    private MainViewModel viewModel;

    private CountDownTimer timer = new CountDownTimer(30000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            binding.tvTimer.setText("seconds remaining: " + millisUntilFinished / 1000);
        }

        @Override
        public void onFinish() {
            viewModel.getUser().setFallen(true);
            Navigation.findNavController(getView()).navigate(R.id.action_fallFragment_to_alertFragment);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fall, container, false);
        binding.btnImOkay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User.getInstance().setFallen(false);
                Navigation.findNavController(view).navigate(R.id.action_fallFragment_to_trackFragment);
                timer.cancel();
            }
        });
        viewModel = new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication()).create(MainViewModel.class);
        timer.start();


        return binding.getRoot();
    }
}