package com.radiumone.sdk.testpush;

import android.app.Application;
import com.radiumone.emitter.push.R1Push;
import com.radiumone.emitter.push.R1PushConfig;
import com.radiumone.sdk.R;

public class R1sdkApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        // drawable in notification bar
        R1PushConfig.getInstance(this).setNotificationIconResourceId(R.drawable.ic_launcher);
        // custom BroadcastReceiver
        R1Push.getInstance(this).setIntentReceiver(TestPushReceiver.class);
    }
}
