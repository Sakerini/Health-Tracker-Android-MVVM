package com.example.healthtracker.view.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.healthtracker.R;
import com.example.healthtracker.databinding.StartActivityBinding;
import com.example.healthtracker.service.viewmodels.StarterViewModel;

public class StartActivity extends AppCompatActivity {


    private StarterViewModel viewModel;
    private StartActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new StarterViewModel(getApplication());
        binding = DataBindingUtil.setContentView(this, R.layout.start_activity);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        viewModel.getUserInformation();
    }
}