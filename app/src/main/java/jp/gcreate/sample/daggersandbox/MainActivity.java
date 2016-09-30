package jp.gcreate.sample.daggersandbox;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import jp.gcreate.sample.daggersandbox.databinding.ActivityMainBinding;
import jp.gcreate.sample.daggersandbox.di.ActivityComponent;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    ActivityComponent component;
    @Inject
    String injectedString;
    @Inject
    DummyPojo pojo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        component = MyApplication.getActivityComponent(this);
        component.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
