package com.radiumone.sdk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.radiumone.sdk.testemitter.EmitterTestStartActivity;
import com.radiumone.sdk.testpush.R1PushTest;


public class MainActivity extends Activity implements View.OnClickListener{

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_layout);
        findViewById(R.id.start_emitter_test).setOnClickListener(this);
        findViewById(R.id.start_push_test).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = new Intent();
        if (id == R.id.start_push_test ){
            intent.setClass(this, R1PushTest.class);
        } else if ( id == R.id.start_emitter_test ){
            intent.setClass(this, EmitterTestStartActivity.class);
        }
        startActivity(intent);
    }
}