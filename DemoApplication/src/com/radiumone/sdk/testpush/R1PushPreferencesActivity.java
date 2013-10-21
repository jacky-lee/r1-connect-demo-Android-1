package com.radiumone.sdk.testpush;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.radiumone.emitter.R1Emitter;
import com.radiumone.emitter.location.LocationPreferences;
import com.radiumone.emitter.push.R1Push;
import com.radiumone.emitter.push.R1PushConfig;
import com.radiumone.emitter.push.R1PushPreferences;
import com.radiumone.sdk.R;

public class R1PushPreferencesActivity extends FragmentActivity implements View.OnClickListener, AddTagDialogFragment.OnTagCreatedListener, ChangeUserIdDialogFragment.OnUserIdChangedListener {

    public static final String PREFS = "r1_mc_shared_prefs";
    public static final String AUTO_START = "auto_start";
    private static final String USER_ID = "user_id";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.r1_push_prefs);

        findViewById(R.id.add_tag).setOnClickListener(this);

        findViewById(R.id.change_id).setOnClickListener(this);

        findViewById(R.id.settings).setOnClickListener(this);

        final CompoundButton pushEnabled = (CompoundButton)findViewById(R.id.push_toggle);
        pushEnabled.setChecked(R1PushPreferences.getInstance(this).isPushEnabled());

        final CompoundButton useGPSCompound = (CompoundButton)findViewById(R.id.use_gps);
        boolean useGps = LocationPreferences.getLocationPreferences(this).isEnableGPSLocation();
        useGPSCompound.setChecked(useGps);

        final CompoundButton backgroundLocationCompound = (CompoundButton)findViewById(R.id.background_location);
        boolean backgroundLocation = LocationPreferences.getLocationPreferences(this).isBackgroundEnabled();
        backgroundLocationCompound.setChecked(backgroundLocation);

        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        String userAppId = prefs.getString(USER_ID, null);
        R1Emitter.getInstance().setApplicationUserId(userAppId);
        if ( !TextUtils.isEmpty(userAppId)){
            TextView userIdTextView = (TextView)findViewById(R.id.user_id);
            userIdTextView.setText(userAppId);
        }
    }

    public void onToggleClicked(View view) {
        // Is the toggle on?
        boolean on = ((CompoundButton) view).isChecked();
        int id = view.getId();
        if (on) {
            if ( id == R.id.push_toggle ){
                R1PushPreferences.getInstance(this).pushEnable();
            } else if ( id == R.id.background_location ){
                LocationPreferences.getLocationPreferences(this).setBackgroundEnabled(true);
            } else if ( id == R.id.use_gps ){
                LocationPreferences.getLocationPreferences(this).setEnableGPSLocation(true);
            }
        } else {
            if ( id == R.id.push_toggle ){
                R1PushPreferences.getInstance(this).pushDisable();
            } else if ( id == R.id.background_location ){
                LocationPreferences.getLocationPreferences(this).setBackgroundEnabled(false);
            } else if ( id == R.id.use_gps ){
                LocationPreferences.getLocationPreferences(this).setEnableGPSLocation(false);
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

    @Override
    protected void onResume() {
        super.onResume();
        fillLayout();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if ( id == R.id.change_id ){
            ChangeUserIdDialogFragment changeUserIdDialogFragment = ChangeUserIdDialogFragment.newInstance(this);
            changeUserIdDialogFragment.show(getSupportFragmentManager(), "user_id");
        } else if ( id == R.id.add_tag ){
            AddTagDialogFragment addTagDialogFragment = AddTagDialogFragment.newInstance(this);
            addTagDialogFragment.show(getSupportFragmentManager(), "tag");
        } else if ( id == R.id.settings ){
            SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(AUTO_START, false);
            editor.commit();
            Intent intent = new Intent(this, R1PushTest.class);
            startActivity(intent);
            finish();
        }
    }

    private void fillLayout(){
        final LinearLayout layout = (LinearLayout)findViewById(R.id.tags);
        if( layout != null ){
            layout.removeAllViews();
            String[] tags = R1Push.getInstance(this).getTags(this);
            if (tags != null ){
                for (String strKey:tags ){
                    final View v = View.inflate(this, R.layout.r1_push_tag_item, null);
                    TextView name = (TextView)v.findViewById(R.id.tag_name);
                    if ( name != null ){
                        name.setText(strKey);
                    }
                    View delete = v.findViewById(R.id.delete);
                    if ( delete != null ){
                        delete.setTag(strKey);
                        delete.setOnClickListener( new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if ( v != null && v.getTag() instanceof String){
                                    String deleteKey = (String) v.getTag();
                                    R1Push.getInstance(R1PushPreferencesActivity.this).removeTag(deleteKey);
                                    fillLayout();
                                }
                            }
                        });
                    }
                    layout.addView(v);
                }
            }
        }
    }

    @Override
    public void onTagCreated(String tag) {
        if (!TextUtils.isEmpty(tag)){
            R1Push.getInstance(this).addTag(tag);
            fillLayout();
        }
    }

    @Override
    public void onUserIdChanged(final String userId) {
        if (!TextUtils.isEmpty(userId)){
        	R1Emitter.getInstance().setApplicationUserId(userId);
            SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(USER_ID, userId);
            editor.commit();
            runOnUiThread( new Runnable() {
                @Override
                public void run() {
                    TextView textView = (TextView)findViewById(R.id.user_id);
                    textView.setText(userId);
                }
            });
        }
    }
}