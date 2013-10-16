package com.radiumone.sdk.testemitter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import com.radiumone.sdk.R;

public class EmitterTestStartActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emitter_test_start);
		View buttonProperties = findViewById(R.id.button_properties);
		if ( buttonProperties != null ){
			buttonProperties.setOnClickListener(this);
		}
		View buttonStart = findViewById(R.id.button_start);
		if ( buttonStart != null ){
			buttonStart.setOnClickListener(this);
		}
	}
	
	@Override
	public void onClick(View v) {
		if ( v != null ){
			if ( v.getId() == R.id.button_properties){
				Intent propsActivity = new Intent(this, PropertiesSettings.class);
				startActivity(propsActivity);
			} else if ( v.getId() == R.id.button_start){
				Intent eventsActivity = new Intent(this, SelectEventActivity.class);
				startActivity(eventsActivity);
			}
		}
	}

}
