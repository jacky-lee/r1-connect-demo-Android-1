package com.radiumone.sdk.testemitter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ToggleButton;
import com.radiumone.emitter.R1SocialPermission;
import com.radiumone.sdk.R;

public class SocialPermissionActivity extends Activity implements View.OnClickListener{
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_permission_item);

        findViewById(R.id.add_permission).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if ( v != null && v.getId() == R.id.add_permission){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();

            R1SocialPermission permission = new R1SocialPermission();

            EditText productEditText = (EditText)findViewById(R.id.name_edit_text);
            if ( productEditText != null ){
                if ( productEditText.getText() != null && productEditText.getText().toString() != null ){
                    final String text = productEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(text)){
                        permission.name = text;
                    }
                }
            }

            ToggleButton grantedToggle = (ToggleButton)findViewById(R.id.granted_toggle);
            if ( grantedToggle != null ){
                permission.granted = grantedToggle.isChecked();
            }

            bundle.putSerializable(EmitEventActivity.SOCIAL_PERMISSIONS, permission);
            intent.putExtra(EmitEventActivity.DATA, bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}