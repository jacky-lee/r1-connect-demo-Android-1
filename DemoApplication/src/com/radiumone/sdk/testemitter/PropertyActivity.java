package com.radiumone.sdk.testemitter;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import android.os.Bundle;
import android.app.Activity;
import com.radiumone.sdk.R;

import java.io.Serializable;

public class PropertyActivity extends Activity {

    private String[] data = new String[]{"String", "Long","Double", "Boolean"};

    public static final int STRING = 0;
    public static final int LONG = 1;
    public static final int DOUBLE = 2;
    public static final int BOOLEAN = 3;

    public int position;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_property);

        final Spinner spinner = (Spinner) findViewById(R.id.type_spinner);
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                parent.setSelection(position);
                PropertyActivity.this.position = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final Button saveButton = (Button)findViewById(R.id.save_property_button);
        if ( saveButton != null ){
            saveButton.setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String key = getKey();
                    Serializable val = getValue();
                    if ( key != null && !key.trim().equals("") && val != null ){
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(EmitEventActivity.KEY, key.trim());
                        bundle.putSerializable(EmitEventActivity.VALUE, val);
                        intent.putExtra(EmitEventActivity.DATA, bundle);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    } else if ( key == null || key.trim().equals("")){
                        Toast.makeText(PropertyActivity.this, "Key value can't be empty", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        EditText valEditText = (EditText)findViewById(R.id.value_edit_text);
        if ( valEditText != null ){
            if (TextUtils.isEmpty(valEditText.getText())){
                valEditText.setText(Util.getRandomString());
            }
        }

        EditText keyEditText = (EditText)findViewById(R.id.key_edit_text);
        if ( keyEditText != null ){
            if ( TextUtils.isEmpty(keyEditText.getText())){
                keyEditText.setText(Util.getRandomString());
            }
        }
	}

    private Serializable getValue() {
        EditText valEditText = (EditText)findViewById(R.id.value_edit_text);
        if ( valEditText != null ){
            if ( valEditText.getText() != null ){
                String valString = valEditText.getText().toString().trim();
                if ( position == STRING ){
                    return valString;
                } else if ( position == LONG ){
                    try{
                        return Long.parseLong(valString);
                    } catch (Exception ex){
                        Toast.makeText(this, "Value not long", Toast.LENGTH_SHORT).show();
                    }
                } else if ( position == DOUBLE ){
                    try{
                        return Double.parseDouble(valString);
                    } catch (Exception ex){
                        Toast.makeText(this, "Value not double", Toast.LENGTH_SHORT).show();
                    }
                } else if ( position == BOOLEAN ){
                    if ( valString != null ){
                        if ( "true".equals(valString) || "1".equals(valString)){
                            return true;
                        } else if ( "false".equals(valString) || "0".equals(valString)){
                            return false;
                        }
                    } else {
                        Toast.makeText(this, "Value not boolean", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        return null;
    }


    public String getKey() {
        EditText keyEditText = (EditText)findViewById(R.id.key_edit_text);
        if ( keyEditText != null ){
            if ( keyEditText.getText() != null ){
                return keyEditText.getText().toString();
            }
        }
        return null;
    }
}
