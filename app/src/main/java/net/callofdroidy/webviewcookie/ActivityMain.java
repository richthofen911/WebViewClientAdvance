package net.callofdroidy.webviewcookie;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ActivityMain extends AppCompatActivity {

    TextView tv_resp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String userId = getIntent().getStringExtra("UserID");

        tv_resp = (TextView) findViewById(R.id.tv_TabScreens);
        tv_resp.setText("UserID: " + userId);

    }

}
