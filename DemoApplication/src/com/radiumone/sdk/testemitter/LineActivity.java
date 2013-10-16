package com.radiumone.sdk.testemitter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import com.radiumone.emitter.R1EmitterLineItem;
import com.radiumone.sdk.R;

import java.util.HashMap;
import java.util.Set;

public class LineActivity extends Activity implements View.OnClickListener {

    private static final int REQUEST_CODE_PARAMETERS = 1;

    public static final String KEY = "key";
    public static final String VALUE = "value";
    public static final String DATA = "property_data";
    private HashMap<String, Object> parameters = new HashMap<String, Object>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_line_item);

        if ( savedInstanceState != null ){
            try{
                parameters = (HashMap<String, Object>) savedInstanceState.getSerializable("parameters");
            } catch ( Exception ex){

            }
        }
        Button save = (Button)findViewById(R.id.save_line);
        if ( save != null ){
            save.setOnClickListener(this);
        }
        Button addParameter = (Button)findViewById(R.id.add_parameter);
        if ( addParameter != null ){
            addParameter.setOnClickListener(this);
        }
        EditText productEditText = (EditText)findViewById(R.id.product_id_edit_text);
        if ( productEditText != null ){
            if ( TextUtils.isEmpty(productEditText.getText())){
                productEditText.setText(Util.getRandomString());
            }
        }

        EditText productNameEditText = (EditText)findViewById(R.id.product_name_edit_text);
        if ( productNameEditText != null ){
            if ( TextUtils.isEmpty(productNameEditText.getText())){
                productNameEditText.setText(Util.getRandomString());
            }
        }

        EditText quantityEditText = (EditText)findViewById(R.id.quantity_edit_text);
        if ( quantityEditText != null ){
            if ( TextUtils.isEmpty(quantityEditText.getText())){
                quantityEditText.setText("" + Math.abs(Util.getRandomInteger()));
            }
        }

        EditText unitsOfMeasureEditText = (EditText)findViewById(R.id.units_of_measure_edit_text);
        if ( unitsOfMeasureEditText != null ){
            if ( TextUtils.isEmpty(unitsOfMeasureEditText.getText())){
                unitsOfMeasureEditText.setText(Util.getRandomString());
            }
        }

        EditText msrPriceEditText = (EditText)findViewById(R.id.msr_price_edit_text);
        if ( msrPriceEditText != null ){
            if ( TextUtils.isEmpty(msrPriceEditText.getText())){
                msrPriceEditText.setText("" + Math.abs(Util.getRandomFloat()));
            }
        }

        EditText pricePaidEditText = (EditText)findViewById(R.id.price_paid_edit_text);
        if ( pricePaidEditText != null ){
            if ( TextUtils.isEmpty(pricePaidEditText.getText())){
                pricePaidEditText.setText("" + Math.abs(Util.getRandomFloat()));
            }
        }

        EditText currencyEditText = (EditText)findViewById(R.id.currency_edit_text);
        if ( currencyEditText != null ){
            if ( TextUtils.isEmpty(currencyEditText.getText())){
                currencyEditText.setText(Util.getRandomString());
            }
        }

        EditText itemCategoryEditText = (EditText)findViewById(R.id.item_category_edit_text);
        if ( itemCategoryEditText != null ){
            if ( TextUtils.isEmpty(itemCategoryEditText.getText())){
                itemCategoryEditText.setText(Util.getRandomString());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillLayoutParameters();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("parameters", parameters);
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
                        delete.setOnClickListener( new View.OnClickListener() {
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
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onClick(View v) {
        if ( v != null && v.getId() == R.id.add_parameter){
            Intent intent = new Intent(this, PropertyActivity.class);
            startActivityForResult(intent, REQUEST_CODE_PARAMETERS);
        } else if ( v != null && v.getId() == R.id.save_line){
            Intent intent = new Intent();
            Bundle bundle = new Bundle();

            R1EmitterLineItem lineItem = new R1EmitterLineItem();

            EditText productEditText = (EditText)findViewById(R.id.product_id_edit_text);
            if ( productEditText != null ){
                if ( productEditText.getText() != null && productEditText.getText().toString() != null ){
                    final String text = productEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(text)){
                        lineItem.productId = text;
                    }
                }
            }

            EditText productNameEditText = (EditText)findViewById(R.id.product_name_edit_text);
            if ( productNameEditText != null ){
                if ( productNameEditText.getText() != null && productNameEditText.getText().toString() != null ){
                    final String text = productNameEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(text)){
                        lineItem.productName = text;
                    }
                }
            }

            EditText quantityEditText = (EditText)findViewById(R.id.quantity_edit_text);
            if ( quantityEditText != null ){
                if ( quantityEditText.getText() != null && quantityEditText.getText().toString() != null ){
                    final String text = quantityEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(text)){
                        try{
                            lineItem.quantity = Integer.parseInt(text);
                        } catch (Exception ex){
                            Toast.makeText(this, "Quantity value wrong", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
            }

            EditText unitsOfMeasureEditText = (EditText)findViewById(R.id.units_of_measure_edit_text);
            if ( unitsOfMeasureEditText != null ){
                if ( unitsOfMeasureEditText.getText() != null && unitsOfMeasureEditText.getText().toString() != null ){
                    final String text = unitsOfMeasureEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(text)){
                        lineItem.unitOfMeasure = text;
                    }
                }
            }

            EditText msrPriceEditText = (EditText)findViewById(R.id.msr_price_edit_text);
            if ( msrPriceEditText != null ){
                if ( msrPriceEditText.getText() != null && msrPriceEditText.getText().toString() != null ){
                    final String text = msrPriceEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(text)){
                        try{
                            lineItem.msrPrice = Float.parseFloat(text);
                        } catch (Exception ex){
                            Toast.makeText(this, "MSR Price value wrong", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
            }

            EditText pricePaidEditText = (EditText)findViewById(R.id.price_paid_edit_text);
            if ( pricePaidEditText != null ){
                if ( pricePaidEditText.getText() != null && pricePaidEditText.getText().toString() != null ){
                    final String text = pricePaidEditText.getText().toString().trim();
                    if (!TextUtils.isEmpty(text)){
                        try{
                            lineItem.pricePaid = Float.parseFloat(text);
                        } catch (Exception ex){
                            Toast.makeText(this, "Price Paid value wrong", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }
            }

            EditText currencyEditText = (EditText)findViewById(R.id.currency_edit_text);
            if ( currencyEditText != null && currencyEditText.getText() != null){
                final String text = currencyEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(text)){
                    lineItem.currency = text;
                }
            }

            EditText itemCategoryEditText = (EditText)findViewById(R.id.item_category_edit_text);
            if ( itemCategoryEditText != null && currencyEditText.getText() != null){
                final String text = currencyEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(text)){
                    lineItem.itemCategory = text;
                }
            }

            lineItem.otherInfo = parameters;
            bundle.putSerializable(EmitEventActivity.LINE_ITEM, lineItem);
            intent.putExtra(EmitEventActivity.DATA, bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}