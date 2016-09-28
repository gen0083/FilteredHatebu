package jp.gcreate.sample.daggersandbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import javax.inject.Inject;

import jp.gcreate.sample.daggersandbox.di.AppComponent;

public class MainActivity extends AppCompatActivity {

    AppComponent component;
    @Inject
    String injectedString;
    @Inject
    DummyPojo pojo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        component = MyApplication.getAppComponent(this);
        component.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((TextView)findViewById(R.id.text)).setText(injectedString);
        ((TextView)findViewById(R.id.text2)).setText(pojo.toString());
    }
}
