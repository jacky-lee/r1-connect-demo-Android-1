package com.radiumone.sdk.testpush;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.radiumone.emitter.R1Emitter;
import com.radiumone.emitter.utils.Utils;
import com.radiumone.sdk.R;

import java.util.Set;

public class ShowNotificationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r1_push_notification_layout);

        // close all notifications for this application (optional)
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        Bundle bundle = getIntent().getExtras();
        if ( bundle != null ){
            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.notifications);
            Object all = bundle.get("all");
            if ( all != null && all instanceof Intent){
                Intent intent = (Intent)all;
                Bundle extras = intent.getExtras();
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    TextView textView = new TextView(this);
                    textView.setText(key + "=" + extras.get(key));
                    linearLayout.addView(textView);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        R1Emitter.getInstance().onStart(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        R1Emitter.getInstance().onStop(this);
    }
}
