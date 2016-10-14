package jp.gcreate.product.filteredhatebu;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import javax.inject.Inject;

import jp.gcreate.product.filteredhatebu.databinding.ActivityLoginBinding;
import jp.gcreate.product.filteredhatebu.di.ActivityComponent;
import jp.gcreate.product.filteredhatebu.di.qualifier.ActivityContext;

/**
 * Copyright 2016 G-CREATE
 */

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    private ActivityComponent component;
    @Inject
    @ActivityContext
    Context   activityContext;
    @Inject
    DummyPojo pojo;
    @Inject
    String    timeString;
    @Inject
    long      time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        component = MyApplication.getActivityComponent(this);
        component.inject(this);

        binding.fromAppComponent.setText("from app:" + pojo + ", activity context:" + activityContext);
        binding.fromActivityComponent.setText("from activity:" + timeString + ", long:" + time);

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = binding.username.getText().toString();
                String password = binding.password.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    binding.usernameWrapper.setError("Input name");
                    binding.usernameWrapper.setErrorEnabled(true);
                } else {
                    binding.usernameWrapper.setErrorEnabled(false);
                }
                if (TextUtils.isEmpty(password)) {
                    binding.passwordWrapper.setError("Input password");
                }
                login(name, password);
            }
        });
    }

    private void login(String name, String password) {
        if (name.equals("test") && password.equals("test")) {
            // success
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Snackbar.make(binding.getRoot(), "Login failed.", Snackbar.LENGTH_SHORT);
        }
    }
}
