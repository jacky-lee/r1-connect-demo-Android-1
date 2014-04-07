package com.radiumone.sdk.testpush;

import android.app.Application;
import com.radiumone.emitter.R1Emitter;
import com.radiumone.sdk.R;

public class R1sdkApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        // drawable in notification bar
        R1Emitter.getInstance().setNotificationIconResourceId(this, R.drawable.notification_icon);
        // custom BroadcastReceiver
        R1Emitter.getInstance().setIntentReceiver(this, TestPushReceiver.class);
        R1Emitter.getInstance().connect(this);
    }
}
