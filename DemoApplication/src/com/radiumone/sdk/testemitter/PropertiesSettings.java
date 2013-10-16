package com.radiumone.sdk.testemitter;

import android.view.View;
import android.widget.Button;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.EditText;
import com.radiumone.emitter.R1Emitter;
import com.radiumone.sdk.R;

public class PropertiesSettings extends Activity implements View.OnClickListener {

	private EditText emitterIdEditText;
	private EditText appVersioneditText;
    private Button resetButton;
    private EditText appUserIdEditText;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_properties_settings);
		
		emitterIdEditText = (EditText)findViewById(R.id.emitter_id_edit_text);
		
		appVersioneditText = (EditText)findViewById(R.id.app_version_edit_text);
        appUserIdEditText = (EditText)findViewById(R.id.app_user_id);

        SharedPreferences prefs = getSharedPreferences(AppPreference.PREF_NAME, MODE_PRIVATE);
        if ( emitterIdEditText != null ){
            emitterIdEditText.setText(R1Emitter.getInstance().getEmitterId(this));
        }
        if ( appVersioneditText != null ){
            appVersioneditText.setText(prefs.getString(AppPreference.APP_VERSION, AppPreference.DEFAULT_VERSION));
        }

        if ( appUserIdEditText != null ){
            appUserIdEditText.setText(prefs.getString(AppPreference.APP_USER_ID, null));
        }
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		SharedPreferences prefs = getSharedPreferences(AppPreference.PREF_NAME, MODE_PRIVATE);
		if ( prefs != null ){
			Editor editor = prefs.edit();
			if ( emitterIdEditText != null ){
				if ( emitterIdEditText.getText() != null ){
					String emitterId = emitterIdEditText.getText().toString();
					R1Emitter.getInstance().startWithEmitterId(this, emitterId, false);
				}
			}
			if ( appVersioneditText != null ){
				if ( appVersioneditText.getText() != null ){
					String appVersion = appVersioneditText.getText().toString();
					editor.putString(AppPreference.APP_VERSION, appVersion);
				}
			}

            if ( appUserIdEditText != null ){
                if ( appUserIdEditText.getText() != null ){
                    String appUserId = appUserIdEditText.getText().toString().trim();
                    editor.putString(AppPreference.APP_USER_ID, appUserId);
                }
            }
			editor.commit();
		}
        R1Emitter.getInstance().resetAppVersion();
	}

    @Override
    public void onClick(View v) {
        if ( appVersioneditText != null ){
            appVersioneditText.setText(AppPreference.DEFAULT_VERSION);
        }
        if ( appUserIdEditText != null ){
            appUserIdEditText.setText(null);
        }
    }
}
