package com.radiumone.sdk.testemitter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.*;
import com.radiumone.emitter.*;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import com.radiumone.sdk.R;

import java.util.*;

public class EmitEventActivity extends Activity implements OnClickListener{

    public static final int REQUEST_CODE_PARAMETERS = 12;
    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String DATA = "property_data";
    public static final String LINE_ITEM = "line_item";
    public static final String SOCIAL_PERMISSIONS = "social_permissions";
    public static final String PARAMETERS = "parameters";

    public static final int DIALOG_DOWNLOAD_PROGRESS = 1;
    private static final int REQUEST_CODE_LINEITEMS = 2;
    private static final int REQUEST_CODE_SOCIAL_ITEMS = 3;

    private int eventType = 0;
    private HashMap<String, Object> parameters = new HashMap<String, Object>();
    private ProgressDialog mProgressDialog;
    private R1EmitterLineItem lineItem;

    private ArrayList<R1SocialPermission> socialPermissions = new ArrayList<R1SocialPermission>();

    public static class Type {
		public static final int EVENT = 0;
		public static final int EVENT_WITH_PARAMETERS = 1;
		public static final int LOGIN = 2;
        public static final int USER_INFO = 3;
		public static final int REGISTRATION = 4;
		public static final int FB_CONNECT = 5;
		public static final int T_CONNECT = 6;
		public static final int TRANSACTION = 7;
        public static final int TRANSACTION_ITEM = 8;
		public static final int CART_CREATE = 9;
		public static final int CART_DELETE = 10;
		public static final int ADD_TO_CART = 11;
		public static final int DELETE_FROM_CART = 12;
        public static final int APP_SCREEN = 13;

        public static final int ACTION_EVENT = 14;
        public static final int UPGRADE = 15;
        public static final int TRIAL_UPGRADE = 16;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences(AppPreference.PREF_NAME, Context.MODE_PRIVATE);
        String appUserId = prefs.getString(AppPreference.APP_USER_ID, null);
        if ( !TextUtils.isEmpty(appUserId)){
            R1Emitter.getInstance().setApplicationUserId(appUserId);
        }
        if ( TextUtils.isEmpty(R1Emitter.getInstance().getApplicationId(this))){
            Toast.makeText(this, "No Application ID selected in Emitter Properties", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        setContentView(R.layout.activity_emit_event);
		Intent intent = getIntent();
		if ( intent != null ){
			Bundle bundle = intent.getExtras();
			if ( bundle != null ){
				eventType = bundle.getInt("type");
			}
		}
		fillData(eventType);
        if ( savedInstanceState != null ){
            try{
                parameters = (HashMap<String, Object>) savedInstanceState.getSerializable(PARAMETERS);
            } catch ( Exception ex){

            }

            try{
                lineItem = (R1EmitterLineItem) savedInstanceState.getSerializable(LINE_ITEM);
            } catch ( Exception ex){

            }

            try{
                socialPermissions = (ArrayList <R1SocialPermission>)savedInstanceState.getSerializable(SOCIAL_PERMISSIONS);
            } catch ( Exception ex){

            }
        }
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == REQUEST_CODE_PARAMETERS){
            if ( resultCode == Activity.RESULT_OK ){
                if (data != null ){
                    Bundle bundle = data.getExtras();
                    if ( bundle != null && bundle.containsKey(DATA)){
                        Bundle bndl = bundle.getBundle(DATA);
                        if ( bndl != null ){
                            final String key = bndl.getString(KEY);
                            if ( key != null ){
                                final Object value = bndl.getSerializable(VALUE);
                                if ( value != null ){
                                    parameters.put(key, value);
                                }
                            }
                        }
                    }
                }
            }
        } else if ( requestCode == REQUEST_CODE_LINEITEMS){
            if ( resultCode == Activity.RESULT_OK ){
                if (data != null ){
                    Bundle bundle = data.getExtras();
                    if ( bundle != null && bundle.containsKey(DATA)){
                        Bundle bndl = bundle.getBundle(DATA);
                        if ( bndl != null ){
                            final R1EmitterLineItem line = (R1EmitterLineItem) bndl.getSerializable(LINE_ITEM);
                            if ( line != null ){
                                lineItem = line;
                            }
                        }
                    }
                }
            }
        } else if ( requestCode == REQUEST_CODE_SOCIAL_ITEMS){
            if ( resultCode == Activity.RESULT_OK ){
                if (data != null ){
                    Bundle bundle = data.getExtras();
                    if ( bundle != null && bundle.containsKey(DATA)){
                        Bundle bndl = bundle.getBundle(DATA);
                        if ( bndl != null ){
                            final R1SocialPermission socialPermission = (R1SocialPermission) bndl.getSerializable(SOCIAL_PERMISSIONS);
                            if ( socialPermission != null ){
                                socialPermissions.add(socialPermission);
                            }
                        }
                    }
                }
            }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        R1Emitter.getInstance().onStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillLayoutParameters();
    }

    private void fillLayoutParameters() {
        final LinearLayout layout = (LinearLayout)findViewById(R.id.parameters_layout);
        if( layout != null ){
            layout.removeAllViews();
            if (parameters != null ){
                final Set<String> keySet = parameters.keySet();
                for (String strKey:keySet ){
                    final View v = View.inflate(this, R.layout.property_item, null);
                    TextView name = (TextView)v.findViewById(R.id.event_name);
                    if ( name != null ){
                        name.setText("KEY: "+strKey);
                    }
                    View delete = v.findViewById(R.id.delete);
                    if ( delete != null ){
                        delete.setTag(strKey);
                        delete.setOnClickListener( new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if ( v != null && v.getTag() instanceof String){
                                    String deleteKey = (String) v.getTag();
                                    parameters.remove(deleteKey);
                                    fillLayoutParameters();
                                }
                            }
                        });
                    }
                    TextView val = (TextView)v.findViewById(R.id.event_type);
                    if ( val != null ){
                        val.setText("VALUE: " +  parameters.get(strKey));
                    }
                    layout.addView(v);
                }
            }
        }

        final LinearLayout lineItemsLayout = (LinearLayout)findViewById(R.id.lineitems_layout);
        if( lineItemsLayout != null && lineItem != null ){
            lineItemsLayout.removeAllViews();
            final View v = View.inflate(this, R.layout.line_item, null);
            TextView name = (TextView)v.findViewById(R.id.line_name);
            if ( name != null ){
                name.setText("Line Name: "+ lineItem.productName);
            }
            View delete = v.findViewById(R.id.delete);
            if ( delete != null ){
                delete.setOnClickListener( new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ( v != null && v.getId() == R.id.delete){
                            lineItem = null;
                            lineItemsLayout.removeAllViews();
                            fillLayoutParameters();
                        }
                    }
                });
            }
            lineItemsLayout.addView(v);
        }

        final LinearLayout socialPermissionsLayout = (LinearLayout)findViewById(R.id.permissions_layout);
        if( socialPermissionsLayout != null ){
            socialPermissionsLayout.removeAllViews();
            int i = 0;
            for (R1SocialPermission socialPermission:socialPermissions){
                final View v = View.inflate(this, R.layout.line_item, null);
                TextView name = (TextView)v.findViewById(R.id.line_name);
                if ( name != null ){
                    name.setText("Social Permission: "+ socialPermission.name);
                }
                View delete = v.findViewById(R.id.delete);
                if ( delete != null ){
                    delete.setTag(i);
                    delete.setOnClickListener( new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if ( v != null && v.getTag() instanceof Integer){
                                int index = (Integer)v.getTag();
                                socialPermissions.remove(index);
                                fillLayoutParameters();
                            }
                        }
                    });
                }
                socialPermissionsLayout.addView(v);
                i++;
            }
        }

        if ( eventType == Type.LOGIN ){

            final EditText userIdEditText = (EditText)findViewById(R.id.user_id_edit_text);
            if ( userIdEditText != null && userIdEditText.getText() != null ){
                String userId = userIdEditText.getText().toString();
                if ( TextUtils.isEmpty(userId)){
                    userIdEditText.setText(Util.getRandomString());
                }
            }
            final EditText userNameEditText = (EditText)findViewById(R.id.user_name_edit_text);
            if ( userNameEditText != null && userNameEditText.getText() != null){
                String userName = userNameEditText.getText().toString();
                if ( TextUtils.isEmpty(userName)){
                    userNameEditText.setText(Util.getRandomString());
                }
            }
        } else if (eventType == Type.ACTION_EVENT){
            final EditText actionEditText = (EditText)findViewById(R.id.action_edit_text);
            if ( actionEditText != null && actionEditText.getText() != null ){
                actionEditText.setText(Util.getRandomString());
            }

            final EditText labelEditText = (EditText)findViewById(R.id.label_edit_text);
            if ( labelEditText != null && labelEditText.getText() != null ){
                labelEditText.setText(Util.getRandomString());
            }

            final EditText valueEditText = (EditText)findViewById(R.id.value_edit_text);
            if ( valueEditText != null && valueEditText.getText() != null ){
                valueEditText.setText("" +  Math.abs(Util.getRandomLong()));
            }
        } else if ( eventType == Type.REGISTRATION ){

            final EditText userIdEditText = (EditText)findViewById(R.id.user_id_edit_text);
            if ( userIdEditText != null && userIdEditText.getText() != null ){
                String id = userIdEditText.getText().toString();
                if ( TextUtils.isEmpty(id)){
                    userIdEditText.setText(Util.getRandomString());
                }
            }
            final EditText userNameEditText = (EditText)findViewById(R.id.user_name_edit_text);
            if ( userNameEditText != null ){
                if (TextUtils.isEmpty(userNameEditText.getText())){
                    userNameEditText.setText(Util.getRandomString());
                }
            }

            final EditText countryEditText = (EditText)findViewById(R.id.country_edit_text);
            if ( countryEditText != null  ){
                if (TextUtils.isEmpty(countryEditText.getText())){
                    countryEditText.setText(Util.getRandomString());
                }
            }

            final EditText cityEditText = (EditText)findViewById(R.id.city_edit_text);
            if ( cityEditText != null  ){
                if (TextUtils.isEmpty(cityEditText.getText())){
                    cityEditText.setText(Util.getRandomString());
                }
            }

            final EditText stateEditText = (EditText)findViewById(R.id.state_edit_text);
            if ( stateEditText != null  ){
                if (TextUtils.isEmpty(stateEditText.getText())){
                    stateEditText.setText(Util.getRandomString());
                }
            }
        } else if ( eventType == Type.USER_INFO ){

            final EditText userIdEditText = (EditText)findViewById(R.id.user_id_edit_text);
            if ( userIdEditText != null && userIdEditText.getText() != null ){
                String id = userIdEditText.getText().toString();
                if ( TextUtils.isEmpty(id)){
                    userIdEditText.setText(Util.getRandomString());
                }
            }
            final EditText userNameEditText = (EditText)findViewById(R.id.user_name_edit_text);
            if ( userNameEditText != null ){
                if (TextUtils.isEmpty(userNameEditText.getText())){
                    userNameEditText.setText(Util.getRandomString());
                }
            }

            final EditText firstNameEditText = (EditText)findViewById(R.id.first_name_edit_text);
            if ( firstNameEditText != null  ){
                if (TextUtils.isEmpty(firstNameEditText.getText())){
                    firstNameEditText.setText(Util.getRandomString());
                }
            }

            final EditText lastNameEditText = (EditText)findViewById(R.id.last_name_edit_text);
            if ( lastNameEditText != null  ){
                if (TextUtils.isEmpty(lastNameEditText.getText())){
                    lastNameEditText.setText(Util.getRandomString());
                }
            }

            final EditText emailEditText = (EditText)findViewById(R.id.email_edit_text);
            if ( emailEditText != null  ){
                if (TextUtils.isEmpty(emailEditText.getText())){
                    emailEditText.setText(Util.getRandomString());
                }
            }

            final EditText phoneEditText = (EditText)findViewById(R.id.phone_edit_text);
            if ( phoneEditText != null  ){
                if (TextUtils.isEmpty(phoneEditText.getText())){
                    phoneEditText.setText(""+Util.getRandomLong());
                }
            }

            final EditText streetAddressEditText = (EditText)findViewById(R.id.street_address_edit_text);
            if ( streetAddressEditText != null  ){
                if (TextUtils.isEmpty(streetAddressEditText.getText())){
                    streetAddressEditText.setText(Util.getRandomString());
                }
            }

            final EditText cityEditText = (EditText)findViewById(R.id.city_edit_text);
            if ( cityEditText != null  ){
                if (TextUtils.isEmpty(cityEditText.getText())){
                    cityEditText.setText(Util.getRandomString());
                }
            }

            final EditText stateEditText = (EditText)findViewById(R.id.state_edit_text);
            if ( stateEditText != null  ){
                if (TextUtils.isEmpty(stateEditText.getText())){
                    stateEditText.setText(Util.getRandomString());
                }
            }

            final EditText zipEditText = (EditText)findViewById(R.id.zip_edit_text);
            if ( zipEditText != null  ){
                if (TextUtils.isEmpty(zipEditText.getText())){
                    zipEditText.setText(Util.getRandomString());
                }
            }
        } else if ( eventType == Type.FB_CONNECT ){


        } else if ( eventType == Type.T_CONNECT ){

            final EditText loginNameEditText = (EditText)findViewById(R.id.user_id_edit_text);
            if ( TextUtils.isEmpty(loginNameEditText.getText())){
                loginNameEditText.setText(Util.getRandomString());
            }
            final EditText passwordNameEditText = (EditText)findViewById(R.id.user_name_edit_text);
            if ( TextUtils.isEmpty(passwordNameEditText.getText())){
                passwordNameEditText.setText(Util.getRandomString());
            }

        } else if ( eventType == Type.TRANSACTION ){

            EditText storeIdEditText = (EditText)findViewById(R.id.store_id_edit_text);
            if ( storeIdEditText != null ){
                if ( TextUtils.isEmpty(storeIdEditText.getText())){
                    storeIdEditText.setText(Util.getRandomString());
                }
            }

            EditText storeNameEditText = (EditText)findViewById(R.id.store_name_edit_text);
            if ( storeNameEditText != null ){
                if ( TextUtils.isEmpty(storeNameEditText.getText())){
                    storeNameEditText.setText(Util.getRandomString());
                }
            }

            EditText currencyEditText = (EditText)findViewById(R.id.currency_edit_text);
            if ( currencyEditText != null ){
                if ( TextUtils.isEmpty(currencyEditText.getText())){
                    currencyEditText.setText(Util.getRandomString());
                }
            }

            EditText totalSalesEditText = (EditText)findViewById(R.id.total_sales_edit_text);
            if ( totalSalesEditText != null ){
                if ( TextUtils.isEmpty(totalSalesEditText.getText())){
                    totalSalesEditText.setText("" + Math.abs(Util.getRandomFloat()));
                }
            }

            EditText shippingCostEditText = (EditText)findViewById(R.id.shipping_cost_edit_text);
            if ( shippingCostEditText != null ){
                if ( TextUtils.isEmpty(shippingCostEditText.getText())){
                    shippingCostEditText.setText("" + Util.getRandomFloat());
                }
            }

            EditText transactionTaxtEditText = (EditText)findViewById(R.id.transaction_taxt_edit_text);
            if ( transactionTaxtEditText != null ){
                if ( TextUtils.isEmpty(transactionTaxtEditText.getText())){
                    transactionTaxtEditText.setText("" + Util.getRandomFloat());
                }
            }

            EditText orderEditText = (EditText)findViewById(R.id.order_id_edit_text);
            if ( orderEditText != null ){
                if ( TextUtils.isEmpty(orderEditText.getText())){
                    orderEditText.setText(Util.getRandomString());
                }
            }

            EditText cartIdEditText = (EditText)findViewById(R.id.cart_id_edit_text);
            if ( cartIdEditText != null ){
                if ( TextUtils.isEmpty(cartIdEditText.getText())){
                    cartIdEditText.setText(Util.getRandomString());
                }
            }

            EditText transactionEditText = (EditText)findViewById(R.id.transaction_id_edit_text);
            if ( transactionEditText != null ){
                if ( TextUtils.isEmpty(transactionEditText.getText())){
                    transactionEditText.setText(Util.getRandomString());
                }
            }
        } else if ( eventType == Type.TRANSACTION_ITEM ){

            EditText storeIdEditText = (EditText)findViewById(R.id.store_id_edit_text);
            if ( storeIdEditText != null ){
                if ( TextUtils.isEmpty(storeIdEditText.getText())){
                    storeIdEditText.setText(Util.getRandomString());
                }
            }

            EditText currencyEditText = (EditText)findViewById(R.id.currency_edit_text);
            if ( currencyEditText != null ){
                if ( TextUtils.isEmpty(currencyEditText.getText())){
                    currencyEditText.setText(Util.getRandomString());
                }
            }

            EditText totalSalesEditText = (EditText)findViewById(R.id.total_sales_edit_text);
            if ( totalSalesEditText != null ){
                if ( TextUtils.isEmpty(totalSalesEditText.getText())){
                    totalSalesEditText.setText("" + Math.abs(Util.getRandomFloat()));
                }
            }

            EditText orderEditText = (EditText)findViewById(R.id.order_id_edit_text);
            if ( orderEditText != null ){
                if ( TextUtils.isEmpty(orderEditText.getText())){
                    orderEditText.setText(Util.getRandomString());
                }
            }

            EditText cartIdEditText = (EditText)findViewById(R.id.cart_id_edit_text);
            if ( cartIdEditText != null ){
                if ( TextUtils.isEmpty(cartIdEditText.getText())){
                    cartIdEditText.setText(Util.getRandomString());
                }
            }

            EditText transactionEditText = (EditText)findViewById(R.id.transaction_id_edit_text);
            if ( transactionEditText != null ){
                if ( TextUtils.isEmpty(transactionEditText.getText())){
                    transactionEditText.setText(Util.getRandomString());
                }
            }
        } else if ( eventType == Type.CART_CREATE ){

            EditText storeIdEditText = (EditText)findViewById(R.id.store_id_edit_text);
            if ( storeIdEditText != null ){
                if ( TextUtils.isEmpty(storeIdEditText.getText())){
                    storeIdEditText.setText(Util.getRandomString());
                }
            }

            EditText currencyEditText = (EditText)findViewById(R.id.currency_edit_text);
            if ( currencyEditText != null ){
                if ( TextUtils.isEmpty(currencyEditText.getText())){
                    currencyEditText.setText(Util.getRandomString());
                }
            }

            EditText totalSalesEditText = (EditText)findViewById(R.id.total_sales_edit_text);
            if ( totalSalesEditText != null ){
                if ( TextUtils.isEmpty(totalSalesEditText.getText())){
                    totalSalesEditText.setText("" + Math.abs(Util.getRandomFloat()));
                }
            }

            EditText shippingCostEditText = (EditText)findViewById(R.id.shipping_cost_edit_text);
            if ( shippingCostEditText != null ){
                if ( TextUtils.isEmpty(shippingCostEditText.getText())){
                    shippingCostEditText.setText("" + Math.abs(Util.getRandomFloat()));
                }
            }

            EditText transactionTaxtEditText = (EditText)findViewById(R.id.transaction_taxt_edit_text);
            if ( transactionTaxtEditText != null ){
                if ( TextUtils.isEmpty(transactionTaxtEditText.getText())){
                    transactionTaxtEditText.setText("" + Math.abs(Util.getRandomFloat()));
                }
            }

            EditText orderEditText = (EditText)findViewById(R.id.order_id_edit_text);
            if ( orderEditText != null ){
                if ( TextUtils.isEmpty(orderEditText.getText())){
                    orderEditText.setText(Util.getRandomString());
                }
            }

            EditText cartIdEditText = (EditText)findViewById(R.id.cart_id_edit_text);
            if ( cartIdEditText != null ){
                if ( TextUtils.isEmpty(cartIdEditText.getText())){
                    cartIdEditText.setText(Util.getRandomString());
                }
            }

            EditText transactionEditText = (EditText)findViewById(R.id.transaction_id_edit_text);
            if ( transactionEditText != null ){
                if ( TextUtils.isEmpty(transactionEditText.getText())){
                    transactionEditText.setText(Util.getRandomString());
                }
            }
        } else if ( eventType == Type.CART_DELETE ){
            EditText cartIdEditText = (EditText)findViewById(R.id.cart_id_edit_text);
            if ( cartIdEditText != null ){
                if ( TextUtils.isEmpty(cartIdEditText.getText())){
                    cartIdEditText.setText(Util.getRandomString());
                }
            }

        } else if ( eventType == Type.ADD_TO_CART ){
            EditText cartIdEditText = (EditText)findViewById(R.id.cart_id_edit_text);
            if ( cartIdEditText != null ){
                if ( TextUtils.isEmpty(cartIdEditText.getText())){
                    cartIdEditText.setText(Util.getRandomString());
                }
            }
        } else if ( eventType == Type.DELETE_FROM_CART ){
            EditText cartIdEditText = (EditText)findViewById(R.id.cart_id_edit_text);
            if ( cartIdEditText != null ){
                if ( TextUtils.isEmpty(cartIdEditText.getText())){
                    cartIdEditText.setText(Util.getRandomString());
                }
            }
        } else if ( eventType == Type.APP_SCREEN ){

            final EditText eventNameEditText = (EditText)findViewById(R.id.event_edit_text);
            if ( eventNameEditText != null  ){
                if ( TextUtils.isEmpty(eventNameEditText.getText())){
                    eventNameEditText.setText(Util.getRandomString());
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(PARAMETERS, parameters);
        outState.putSerializable(LINE_ITEM, lineItem);
        outState.putSerializable(SOCIAL_PERMISSIONS, socialPermissions);
    }

    @Override
    protected void onStop() {
        super.onStop();
        R1Emitter.getInstance().onStop(this);
    }

    private void fillData(int eventType) {
		if ( eventType == Type.EVENT ){
			setContentView(R.layout.event);
		} else if ( eventType == Type.EVENT_WITH_PARAMETERS ){
			setContentView(R.layout.event_with_params);
		} else if ( eventType == Type.LOGIN ){
			setContentView(R.layout.login);
		} else if ( eventType == Type.REGISTRATION ){
			setContentView(R.layout.registration);
		} else if ( eventType == Type.USER_INFO ){
            setContentView(R.layout.user_info);
        } else if ( eventType == Type.FB_CONNECT ){
			setContentView(R.layout.fb_connect);
		} else if ( eventType == Type.T_CONNECT ){
			setContentView(R.layout.t_connect);
		} else if ( eventType == Type.TRANSACTION ){
			setContentView(R.layout.transaction);
		} else if ( eventType == Type.TRANSACTION_ITEM ){
            setContentView(R.layout.transaction_item);
        } else if ( eventType == Type.CART_CREATE ){
			setContentView(R.layout.cart_create);
		} else if ( eventType == Type.CART_DELETE ){
			setContentView(R.layout.cart_delete);
		} else if ( eventType == Type.ADD_TO_CART ){
			setContentView(R.layout.add_to_cart);
		} else if ( eventType == Type.DELETE_FROM_CART ){
			setContentView(R.layout.delete_from_cart);
		} else if ( eventType == Type.APP_SCREEN ){
            setContentView(R.layout.app_screen);
        } else if ( eventType == Type.ACTION_EVENT ){
            setContentView(R.layout.action_event);
        } else if ( eventType == Type.UPGRADE ){
            setContentView(R.layout.upgrade);
        } else if ( eventType == Type.TRIAL_UPGRADE ){
            setContentView(R.layout.trial_upgrade);
        }
		Button send = (Button)findViewById(R.id.send_button);
        if ( send != null ){
            send.setOnClickListener(this);
        }
        Button addParameter = (Button)findViewById(R.id.add_parameter);
        if ( addParameter != null ){
            addParameter.setOnClickListener(this);
        }

        Button addPermission = (Button)findViewById(R.id.add_permission);
        if ( addPermission != null ){
            addPermission.setOnClickListener(this);
        }

        Button addLineItem = (Button)findViewById(R.id.add_lineitem);
        if ( addLineItem != null ){
            addLineItem.setOnClickListener(this);
        }
	}

	@Override
	public void onClick(View v) {
		if ( v != null && v.getId() == R.id.send_button){

            Toast toast = Toast.makeText(this, "Sending", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0,0);
            toast.show();
			if ( eventType == Type.EVENT ){
				String eventName = null;

                final EditText eventNameEditText = (EditText)findViewById(R.id.event_edit_text);
                if ( eventNameEditText != null && eventNameEditText.getText() != null ){
                    eventName = eventNameEditText.getText().toString();
                }
                if ( eventName != null ){
				    R1Emitter.getInstance().emitEvent(eventName);
                    R1Emitter.getInstance().send();
                }
			} else if ( eventType == Type.EVENT_WITH_PARAMETERS ){
                String eventName = null;

                final EditText eventNameEditText = (EditText)findViewById(R.id.event_edit_text);
                if ( eventNameEditText != null && eventNameEditText.getText() != null ){
                    eventName = eventNameEditText.getText().toString();
                }
                if ( !TextUtils.isEmpty(eventName) ){
                    R1Emitter.getInstance().emitEvent(eventName, parameters);
                    R1Emitter.getInstance().send();
                } else {
                    Toast.makeText(this, "Empty event", Toast.LENGTH_SHORT).show();
                }
			} else if ( eventType == Type.LOGIN ){
                String userId = null;
                String userName = null;

                final EditText userIdEditText = (EditText)findViewById(R.id.user_id_edit_text);
                if ( userIdEditText != null && userIdEditText.getText() != null ){
                    userId = userIdEditText.getText().toString();
                }
                final EditText userNameEditText = (EditText)findViewById(R.id.user_name_edit_text);
                if ( userNameEditText != null && userNameEditText.getText() != null ){
                    userName = userNameEditText.getText().toString();
                }
                if (!TextUtils.isEmpty(userId) || !TextUtils.isEmpty(userName) ){
                    R1Emitter.getInstance().emitLogin(userId, userName, parameters);
                    R1Emitter.getInstance().send();
                } else {
                    Toast.makeText(this, "Empty userName and userId", Toast.LENGTH_SHORT).show();
                }
			} else if ( eventType == Type.REGISTRATION ){

                R1Emitter.UserItem userItem = new R1Emitter.UserItem();

                final EditText userIdEditText = (EditText)findViewById(R.id.user_id_edit_text);
                if ( userIdEditText != null && userIdEditText.getText() != null ){
                    userItem.userId = userIdEditText.getText().toString();
                }

                final EditText userNameEditText = (EditText)findViewById(R.id.user_name_edit_text);
                if ( userNameEditText != null ){
                    if (!TextUtils.isEmpty(userNameEditText.getText())){
                        userItem.userName = userNameEditText.getText().toString();
                    }
                }

                final EditText countryEditText = (EditText)findViewById(R.id.country_edit_text);
                if ( countryEditText != null  ){
                    if (!TextUtils.isEmpty(countryEditText.getText())){
                        userItem.country = countryEditText.getText().toString();
                    }
                }

                final EditText stateEditText = (EditText)findViewById(R.id.state_edit_text);
                if ( stateEditText != null  ){
                    if (!TextUtils.isEmpty(stateEditText.getText())){
                        userItem.state = stateEditText.getText().toString();
                    }
                }

                final EditText cityEditText = (EditText)findViewById(R.id.city_edit_text);
                if ( cityEditText != null  ){
                    if (!TextUtils.isEmpty(cityEditText.getText())){
                        userItem.city = cityEditText.getText().toString();
                    }
                }

				R1Emitter.getInstance().emitRegistration(userItem.userId, userItem.userName, userItem.country, userItem.city, userItem.state,parameters);
                R1Emitter.getInstance().send();
			} else if ( eventType == Type.USER_INFO ){

                R1Emitter.UserItem userItem = new R1Emitter.UserItem();

                final EditText userIdEditText = (EditText)findViewById(R.id.user_id_edit_text);
                if ( userIdEditText != null && userIdEditText.getText() != null ){
                    userItem.userId = userIdEditText.getText().toString();
                }

                final EditText userNameEditText = (EditText)findViewById(R.id.user_name_edit_text);
                if ( userNameEditText != null ){
                    if (!TextUtils.isEmpty(userNameEditText.getText())){
                        userItem.userName = userNameEditText.getText().toString();
                    }
                }

                final EditText firstNameEditText = (EditText)findViewById(R.id.first_name_edit_text);
                if ( firstNameEditText != null ){
                    if (!TextUtils.isEmpty(firstNameEditText.getText())){
                        userItem.firstName = firstNameEditText.getText().toString();
                    }
                }

                final EditText lastNameEditText = (EditText)findViewById(R.id.last_name_edit_text);
                if ( lastNameEditText != null ){
                    if (!TextUtils.isEmpty(lastNameEditText.getText())){
                        userItem.lastName = lastNameEditText.getText().toString();
                    }
                }

                final EditText emailEditText = (EditText)findViewById(R.id.email_edit_text);
                if ( emailEditText != null  ){
                    if (!TextUtils.isEmpty(emailEditText.getText())){
                        userItem.email = emailEditText.getText().toString();
                    }
                }

                final EditText phoneEditText = (EditText)findViewById(R.id.phone_edit_text);
                if ( phoneEditText != null  ){
                    if (!TextUtils.isEmpty(phoneEditText.getText())){
                        userItem.phone = phoneEditText.getText().toString();
                    }
                }

                final EditText streetEditText = (EditText)findViewById(R.id.street_address_edit_text);
                if ( streetEditText != null  ){
                    if (!TextUtils.isEmpty(streetEditText.getText())){
                        userItem.streetAddress = streetEditText.getText().toString();
                    }
                }


                final EditText stateEditText = (EditText)findViewById(R.id.state_edit_text);
                if ( stateEditText != null  ){
                    if (!TextUtils.isEmpty(stateEditText.getText())){
                        userItem.state = stateEditText.getText().toString();
                    }
                }

                final EditText cityEditText = (EditText)findViewById(R.id.city_edit_text);
                if ( cityEditText != null  ){
                    if (!TextUtils.isEmpty(cityEditText.getText())){
                        userItem.city = cityEditText.getText().toString();
                    }
                }

                final EditText zipEditText = (EditText)findViewById(R.id.zip_edit_text);
                if ( zipEditText != null  ){
                    if (!TextUtils.isEmpty(zipEditText.getText())){
                        userItem.zip = zipEditText.getText().toString();
                    }
                }

                R1Emitter.getInstance().emitUserInfo(userItem ,parameters);
                R1Emitter.getInstance().send();
            } else if ( eventType == Type.FB_CONNECT ){
				R1Emitter.getInstance().emitFBConnect(socialPermissions, parameters);
                R1Emitter.getInstance().send();
			} else if ( eventType == Type.T_CONNECT ){

                R1Emitter.UserItem userItem = new R1Emitter.UserItem();

                final EditText loginNameEditText = (EditText)findViewById(R.id.user_id_edit_text);
                if ( loginNameEditText != null && loginNameEditText.getText() != null ){
                    userItem.userId = loginNameEditText.getText().toString();
                }
                final EditText passwordNameEditText = (EditText)findViewById(R.id.user_name_edit_text);
                if ( passwordNameEditText != null && passwordNameEditText.getText() != null ){
                    userItem.userName = passwordNameEditText.getText().toString();
                }

				R1Emitter.getInstance().emitTConnect(userItem.userId, userItem.userName, socialPermissions, parameters);
                R1Emitter.getInstance().send();
			} else if ( eventType == Type.TRANSACTION ){
                R1Emitter.EmitItem purchaseItem = new R1Emitter.EmitItem();

                EditText storeIdEditText = (EditText)findViewById(R.id.store_id_edit_text);
                if ( storeIdEditText != null ){
                    if ( storeIdEditText.getText() != null && storeIdEditText.getText().toString() != null ){
                        final String text = storeIdEditText.getText().toString().trim();
                        if (!TextUtils.isEmpty(text)){
                            purchaseItem.storeId = text;
                        }
                    }
                }

                EditText storeNameEditText = (EditText)findViewById(R.id.store_name_edit_text);
                if ( storeNameEditText != null ){
                    if ( storeNameEditText.getText() != null && storeNameEditText.getText().toString() != null ){
                        final String text = storeNameEditText.getText().toString().trim();
                        if (!TextUtils.isEmpty(text)){
                            purchaseItem.storeName = text;
                        }
                    }
                }

                EditText currencyEditText = (EditText)findViewById(R.id.currency_edit_text);
                if ( currencyEditText != null ){
                    if ( currencyEditText.getText() != null && currencyEditText.getText().toString() != null ){
                        final String text = currencyEditText.getText().toString().trim();
                        if (!TextUtils.isEmpty(text)){
                            purchaseItem.currency = text;
                        }
                    }
                }

                EditText totalSalesEditText = (EditText)findViewById(R.id.total_sales_edit_text);
                if ( totalSalesEditText != null ){
                    if ( totalSalesEditText.getText() != null && totalSalesEditText.getText().toString() != null ){
                        final String text = totalSalesEditText.getText().toString().trim();
                        if (!TextUtils.isEmpty(text)){
                            try{
                                purchaseItem.totalSale = Float.parseFloat(text);
                            } catch (Exception ex){
                                Toast.makeText(this, "Wrong Total Sales Values", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                }

                EditText shippingCostEditText = (EditText)findViewById(R.id.shipping_cost_edit_text);
                if ( shippingCostEditText != null ){
                    if ( shippingCostEditText.getText() != null && shippingCostEditText.getText().toString() != null ){
                        final String text = shippingCostEditText.getText().toString().trim();
                        if (!TextUtils.isEmpty(text)){
                            try{
                                purchaseItem.shippingCosts = Float.parseFloat(text);
                            } catch (Exception ex){
                                Toast.makeText(this, "Wrong Shipping cost Values", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                }

                EditText transactionTaxEditText = (EditText)findViewById(R.id.transaction_taxt_edit_text);
                if ( transactionTaxEditText != null ){
                    if ( transactionTaxEditText.getText() != null && transactionTaxEditText.getText().toString() != null ){
                        final String text = transactionTaxEditText.getText().toString().trim();
                        if (!TextUtils.isEmpty(text)){
                            try{
                                purchaseItem.transactionTax = Float.parseFloat(text);
                            } catch (Exception ex){
                                Toast.makeText(this, "Wrong Transactions Values", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                    }
                }

                EditText orderEditText = (EditText)findViewById(R.id.order_id_edit_text);
                if ( orderEditText != null ){
                    if ( orderEditText.getText() != null && orderEditText.getText().toString() != null ){
                        final String text = orderEditText.getText().toString().trim();
                        if (!TextUtils.isEmpty(text)){
                            purchaseItem.orderId = text;
                        }
                    }
                }

                EditText cartIdEditText = (EditText)findViewById(R.id.cart_id_edit_text);
                if ( cartIdEditText != null ){
                    if ( cartIdEditText.getText() != null && cartIdEditText.getText().toString() != null ){
                        final String text = cartIdEditText.getText().toString().trim();
                        if (!TextUtils.isEmpty(text)){
                            purchaseItem.cartId = text;
                        }
                    }
                }

                EditText transactionEditText = (EditText)findViewById(R.id.transaction_id_edit_text);
                if ( transactionEditText != null ){
                    if ( transactionEditText.getText() != null && transactionEditText.getText().toString() != null ){
                        final String text = transactionEditText.getText().toString().trim();
                        if (!TextUtils.isEmpty(text)){
                            purchaseItem.transactionId = text;
                        }
                    }
                }

				R1Emitter.getInstance().emitTransaction(purchaseItem, parameters);
                R1Emitter.getInstance().send();
			} else if ( eventType == Type.TRANSACTION_ITEM ){

                EditText transactionEditText = (EditText)findViewById(R.id.transaction_id_edit_text);
                String transactionId = null;
                if ( transactionEditText != null ){
                    if ( transactionEditText.getText() != null && transactionEditText.getText().toString() != null ){
                        final String text = transactionEditText.getText().toString().trim();
                        if (!TextUtils.isEmpty(text)){
                            transactionId = text;
                        }
                    }
                }

                R1Emitter.getInstance().emitTransactionItem(transactionId, lineItem, parameters);
                R1Emitter.getInstance().send();
            } else if ( eventType == Type.CART_CREATE ){
                R1Emitter.EmitItem cartItem = new R1Emitter.EmitItem();

                EditText storeIdEditText = (EditText)findViewById(R.id.cart_id_edit_text);
                if ( storeIdEditText != null ){
                    if ( storeIdEditText.getText() != null && storeIdEditText.getText().toString() != null ){
                        final String text = storeIdEditText.getText().toString().trim();
                        if (!TextUtils.isEmpty(text)){
                            cartItem.cartId = text;
                        }
                    }
                }

				R1Emitter.getInstance().emitCartCreate(cartItem.cartId, parameters);
                R1Emitter.getInstance().send();
			} else if ( eventType == Type.CART_DELETE ){
                EditText cartIdEditText = (EditText)findViewById(R.id.cart_id_edit_text);
                String cartId = null;
                if ( cartIdEditText != null ){
                    if ( cartIdEditText.getText() != null && cartIdEditText.getText().toString() != null ){
                        final String text = cartIdEditText.getText().toString().trim();
                        if (!TextUtils.isEmpty(text)){
                            cartId = text;
                        }
                    }
                }
                if ( cartId != null ){
				    R1Emitter.getInstance().emitCartDelete(cartId, parameters);
                    R1Emitter.getInstance().send();
                }
			} else if ( eventType == Type.ADD_TO_CART ){
                EditText cartIdEditText = (EditText)findViewById(R.id.cart_id_edit_text);
                String cartId = null;
                if ( cartIdEditText != null ){
                    if ( cartIdEditText.getText() != null && cartIdEditText.getText().toString() != null ){
                        final String text = cartIdEditText.getText().toString().trim();
                        if (!TextUtils.isEmpty(text)){
                            cartId = text;
                        }
                    }
                }
                if ( cartId != null ){
				    R1Emitter.getInstance().emitAddToCart(cartId, lineItem, parameters);
                    R1Emitter.getInstance().send();
                }
			} else if ( eventType == Type.DELETE_FROM_CART ){
                EditText cartIdEditText = (EditText)findViewById(R.id.cart_id_edit_text);
                String cartId = null;
                if ( cartIdEditText != null ){
                    if ( cartIdEditText.getText() != null && cartIdEditText.getText().toString() != null ){
                        final String text = cartIdEditText.getText().toString().trim();
                        if (!TextUtils.isEmpty(text)){
                            cartId = text;
                        }
                    }
                }
				R1Emitter.getInstance().emitRemoveFromCart(cartId, lineItem, parameters);
                R1Emitter.getInstance().send();
			} else if ( eventType == Type.APP_SCREEN ){
                String appScreenName = null;

                final EditText eventNameEditText = (EditText)findViewById(R.id.event_name_edit_text);
                if ( eventNameEditText != null && eventNameEditText.getText() != null ){
                    appScreenName = eventNameEditText.getText().toString();
                }

                String contentDescription = null;
                final EditText contentDescriptionEditText = (EditText)findViewById(R.id.content_description_edit_text);
                if ( contentDescriptionEditText != null && contentDescriptionEditText.getText() != null ){
                    contentDescription = contentDescriptionEditText.getText().toString();
                }

                String documentLocationUrl = null;
                final EditText documentLocationEditText = (EditText)findViewById(R.id.document_location_url_edit_text);
                if ( documentLocationEditText != null && documentLocationEditText.getText() != null ){
                    documentLocationUrl = documentLocationEditText.getText().toString();
                }

                String documentHostName = null;
                final EditText documentHostNameEditText = (EditText)findViewById(R.id.document_host_name_edit_text);
                if ( documentHostNameEditText != null && documentHostNameEditText.getText() != null ){
                    documentHostName = documentHostNameEditText.getText().toString();
                }

                String documentPath = null;
                final EditText documentPathEditText = (EditText)findViewById(R.id.document_path_edit_text);
                if ( documentPathEditText != null && documentPathEditText.getText() != null ){
                    documentPath = documentPathEditText.getText().toString();
                }

                if ( !TextUtils.isEmpty(appScreenName) ){
                    R1Emitter.getInstance().emitAppScreen(appScreenName, contentDescription, documentLocationUrl, documentHostName, documentPath, parameters);
                    R1Emitter.getInstance().send();
                } else {
                    Toast.makeText(this, "App screen name required", Toast.LENGTH_SHORT).show();
                }

            } else if ( eventType == Type.ACTION_EVENT ){

                String action = null;
                final EditText actionEditText = (EditText)findViewById(R.id.action_edit_text);
                if ( actionEditText != null && actionEditText.getText() != null ){
                    action = actionEditText.getText().toString();
                }

                String label = null;
                final EditText labelEditText = (EditText)findViewById(R.id.label_edit_text);
                if ( labelEditText != null && labelEditText.getText() != null ){
                    label = labelEditText.getText().toString();
                }

                long value = -1;
                final EditText valueEditText = (EditText)findViewById(R.id.value_edit_text);
                if ( valueEditText != null && valueEditText.getText() != null ){
                    final String text = valueEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(text)){
                        try{
                            value =  Math.abs(Long.parseLong(text));
                        } catch (Exception ex){
                            Toast.makeText(this, "Wrong Value", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                R1Emitter.getInstance().emitAction(action, label, value, parameters);
                R1Emitter.getInstance().send();
            } else if ( eventType == Type.UPGRADE ){
                R1Emitter.getInstance().emitUpgrade(parameters);
                R1Emitter.getInstance().send();
            } else if ( eventType == Type.TRIAL_UPGRADE){
                R1Emitter.getInstance().emitTrialUpgrade(parameters);
                R1Emitter.getInstance().send();
            }
		} else if ( v != null && v.getId() == R.id.add_parameter){
            Intent intent = new Intent(this, PropertyActivity.class);
            startActivityForResult(intent, REQUEST_CODE_PARAMETERS);
        } else if ( v != null && v.getId() == R.id.add_lineitem){
            Intent intent = new Intent(this, LineActivity.class);
            startActivityForResult(intent, REQUEST_CODE_LINEITEMS);
        } else if ( v != null && v.getId() == R.id.add_permission){
            Intent intent = new Intent(this, SocialPermissionActivity.class);
            startActivityForResult(intent, REQUEST_CODE_SOCIAL_ITEMS);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS:
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Request...");
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }

    }
}
