package com.radiumone.sdk.testpush;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.radiumone.emitter.push.R1PushConfig;
import com.radiumone.sdk.R;

public class R1PushTest extends Activity {

    public static final String PREFS = "r1_mc_shared_prefs";
    public static final String AUTO_START = "auto_start";

    private EditText senderIdEditText;
    private EditText appIdEditText;
    private EditText appKeyEditText;
    private Button startButton;
    private SharedPreferences prefs;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r1_push_start);

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        senderIdEditText = (EditText) findViewById(R.id.sender_id);

        String senderId = R1PushConfig.getInstance(this).getSenderId();
        if ( senderId != null ){
            senderIdEditText.setText(senderId);
        }

        appIdEditText = (EditText) findViewById(R.id.app_id);

        String appId = R1PushConfig.getInstance(this).getAppId();
        if ( appId != null ){
            appIdEditText.setText(appId);
        }

        appKeyEditText = (EditText) findViewById(R.id.app_key);

        String appKey = R1PushConfig.getInstance(this).getClientKey();
        if ( appKey != null ){
            appKeyEditText.setText(appKey);
        }

        boolean needDirectStart = prefs.getBoolean(AUTO_START, false);
        if ( needDirectStart ){
            if (!TextUtils.isEmpty(senderId) && !TextUtils.isEmpty(appId) && !TextUtils.isEmpty(appKey)){
                startTest(senderId, appKey, appId);
                finish();
                return;
            }
        }

        startButton = (Button)findViewById(R.id.start_button);
        startButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String senderId = null;
                if ( senderIdEditText.getText() != null ){
                    senderId = senderIdEditText.getText().toString();
                }

                String appId = null;
                if ( appIdEditText.getText() != null ){
                    appId = appIdEditText.getText().toString();
                }

                String appKey = null;
                if ( appKeyEditText.getText() != null ){
                    appKey = appKeyEditText.getText().toString();
                }
                if (!TextUtils.isEmpty(senderId) && !TextUtils.isEmpty(appId) && !TextUtils.isEmpty(appKey)){
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean(AUTO_START, true);
                    editor.commit();
                    startTest(senderId, appKey, appId);
                    finish();
                } else {
                    Toast.makeText(R1PushTest.this, "Wrong data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startTest(String senderId, String clientId, String appId) {
        // this for testing only. Use r1connect.properties
        R1PushConfig.getInstance(R1PushTest.this).setCredentials(this, appId, clientId, false);
        R1PushConfig.getInstance(R1PushTest.this).setSenderId(senderId, false);
        final Intent intent = new Intent(R1PushTest.this, R1PushPreferencesActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}