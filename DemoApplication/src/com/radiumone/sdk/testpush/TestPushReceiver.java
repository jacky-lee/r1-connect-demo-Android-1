package com.radiumone.sdk.testpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.radiumone.emitter.push.R1Push;

public class TestPushReceiver extends BroadcastReceiver {

    public void onReceive(Context context, Intent intent) {
        try{
            Context applicationContex = context.getApplicationContext();
            if ( intent != null ){
                if (intent.getAction().equals(R1Push.OPENED_NOTIFICATION)){
                    Intent openIntent = new Intent(context, ShowNotificationActivity.class);
                    openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    if ( intent.getExtras() != null ){
                        openIntent.putExtra("all", intent);
                    }
                    applicationContex.startActivity(openIntent);
                }
            }
        } catch (Exception ex){

        }
    }
}
