package jp.gcreate.sample.daggersandbox;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import javax.inject.Inject;
import javax.inject.Named;

import jp.gcreate.sample.daggersandbox.databinding.ActivityLoginBinding;
import jp.gcreate.sample.daggersandbox.di.ActivityComponent;

/**
 * Copyright 2016 G-CREATE
 */

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    private ActivityComponent component;
    @Inject @Named("activity")
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        component = MyApplication.getActivityComponent(this);
        component.inject(this);

        Log.d("test", "activity: " + context.toString());
    }
}
