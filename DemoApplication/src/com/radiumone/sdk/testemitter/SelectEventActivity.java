package com.radiumone.sdk.testemitter;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import com.radiumone.sdk.R;

public class SelectEventActivity extends Activity implements OnClickListener {

	private int[] ids = new int[]{R.id.button1,R.id.button2,R.id.button3,R.id.button4,R.id.button5,R.id.button6,R.id.button7,R.id.button8,R.id.button9,R.id.button10,R.id.button11,R.id.button12, R.id.button13, R.id.button14, R.id.button15, R.id.button16};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events);
		for (int id:ids) {
			findViewById(id).setOnClickListener(this);
		}
    }

	@Override
	public void onClick(View v) {
		int id = v.getId();
		Intent eventIntent = new Intent(this, EmitEventActivity.class);
		int intentType = EmitEventActivity.Type.EVENT;
		if ( id == R.id.button2 ){
			intentType = EmitEventActivity.Type.EVENT_WITH_PARAMETERS;
		}else if ( id == R.id.button3 ){
			intentType = EmitEventActivity.Type.LOGIN;
		}else if ( id == R.id.button4 ){
			intentType = EmitEventActivity.Type.REGISTRATION;
		}else if ( id == R.id.button5 ){
			intentType = EmitEventActivity.Type.FB_CONNECT;
		}else if ( id == R.id.button6 ){
			intentType = EmitEventActivity.Type.T_CONNECT;
		}else if ( id == R.id.button7 ){
			intentType = EmitEventActivity.Type.TRANSACTION;
		}else if ( id == R.id.button8 ){
            intentType = EmitEventActivity.Type.TRANSACTION_ITEM;
        }else if ( id == R.id.button9 ){
			intentType = EmitEventActivity.Type.CART_CREATE;
		}else if ( id == R.id.button10 ){
			intentType = EmitEventActivity.Type.CART_DELETE;
		}else if ( id == R.id.button11 ){
			intentType = EmitEventActivity.Type.ADD_TO_CART;
		}else if ( id == R.id.button12 ){
			intentType = EmitEventActivity.Type.DELETE_FROM_CART;
		}else if ( id == R.id.button13 ){
            intentType = EmitEventActivity.Type.ACTION_EVENT;
        }else if ( id == R.id.button14 ){
            intentType = EmitEventActivity.Type.UPGRADE;
        }else if ( id == R.id.button15 ){
            intentType = EmitEventActivity.Type.TRIAL_UPGRADE;
        }else if ( id == R.id.button16 ){
            intentType = EmitEventActivity.Type.APP_SCREEN;
        }
		eventIntent.putExtra("type", intentType);
		startActivity(eventIntent);
	}

}
