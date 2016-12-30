package com.app.imagecreator.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amapps.imagecreator.R;
import com.app.imagecreator.PaintApplication;
import com.app.imagecreator.adapters.CustomListTextAdapter;
import com.app.imagecreator.dialogs.ColorPickerDialog;
import com.app.imagecreator.dialogs.ColorPickerDialog.OnColorDeSelectedListener;
import com.app.imagecreator.dialogs.ColorPickerDialog.OnColorSelectedListener;
import com.app.imagecreator.utility.Constant;
import com.app.imagecreator.utility.OnDialogClick;
import com.app.imagecreator.utility.Utility;
import com.google.android.gms.ads.AdView;

public class TextActivity extends Activity implements OnClickListener, OnItemClickListener, Constant, OnDialogClick, TextWatcher {

    private ImageView imgCancel = null, imgSave = null, imgColorPicker = null;
    private TextView txtPreviewText = null;
    private EditText edtUsersText = null;
    private ListView listTextType = null;
    private String type = "Typeface.DEFAULT";
    private String userText = null;
    private int txtColor = 0;
    private String path = null, imgName = null;
    private boolean editMode = false;
    private AdView adView = null;
    private LinearLayout linAdView = null;

    String[] itemTypes = new String[]{"AshleyScriptMTStd.otf", "Aerovias_Brasil_NF.ttf", "ANASTAS.TTF", "Carrington.ttf",
            "children stories.ttf", "INDIGO.TTF", "Lobster_1.3.otf",
            "msd_Regular.ttf", "Squares Bold Free.otf", "ZAFCHAMI.TTF"};

    String[] itemNames = new String[]{"Ashley Script MTStd", "Aerovias Brasil NF", "ANASTAS", "Carrington",
            "children stories", "INDIGO", "Lobster 1.3",
            "msd Regular", "Squares Bold Free", "ZAFCHAMI"};

    CustomListTextAdapter customListTextAdapter = new CustomListTextAdapter(this, itemTypes, itemNames);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        initControls();
    }

    private void initControls() {

        linAdView = (LinearLayout) findViewById(R.id.linAdView);
        adView = (AdView) findViewById(R.id.adView);
        if (!PaintApplication.preferenceData.isPurchased()) {
            linAdView.setVisibility(View.VISIBLE);
            Utility.loadAdd(adView);
        }

        imgCancel = (ImageView) findViewById(R.id.imgCancel);
        imgCancel.setOnClickListener(this);

        imgSave = (ImageView) findViewById(R.id.imgSave);
        imgSave.setOnClickListener(this);

        imgColorPicker = (ImageView) findViewById(R.id.imgColorPicker);
        imgColorPicker.setOnClickListener(this);

        txtPreviewText = (TextView) findViewById(R.id.txtPreviewText);
        Typeface tf1 = Typeface.createFromAsset(this.getAssets(), itemTypes[0]);
        txtPreviewText.setTypeface(tf1);

        edtUsersText = (EditText) findViewById(R.id.edtUsersText);

        Bundle bundle = getIntent().getExtras();
        path = bundle.getString(PATH);
        imgName = bundle.getString(IMAGE_NAME);
        if (bundle.getString(USER_TEXT) != null) {
            editMode = true;
            userText = bundle.getString(USER_TEXT);
            txtPreviewText.setText(userText);
            edtUsersText.setText(userText);
            if (bundle.getInt(TEXT_COLOR) != 0) {
                txtColor = bundle.getInt(TEXT_COLOR);
                txtPreviewText.setTextColor(txtColor);
            }
            if (bundle.getString(TEXT_TYPE) != null) {
                type = bundle.getString(TEXT_TYPE);
                Typeface tf = Typeface.createFromAsset(this.getAssets(), type);
                txtPreviewText.setTypeface(tf);
            }
        }

        edtUsersText.addTextChangedListener(this);

        listTextType = (ListView) findViewById(R.id.listTextType);

        listTextType.setAdapter(customListTextAdapter);

        listTextType.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.imgCancel:
                if (!editMode)
                    finish();
                else
                    finish();
                break;

            case R.id.imgSave:
                userText = edtUsersText.getText().toString();
                if (Utility.isNotNull(userText)) {
                    callAddTextActivity();
                } else {
                    Utility.openAlertDialog(this, null,
                            getString(R.string.please_enter_any_text), 589);
                }
                break;

            case R.id.imgColorPicker:
                openColorPicker(R.id.imgColorPicker);
                break;
            default:
                break;
        }
    }

    private void callAddTextActivity() {
        Intent intent = new Intent(TextActivity.this, AddTextActivity.class);
        intent.putExtra(PATH, path);
        intent.putExtra(IMAGE_NAME, imgName);
        intent.putExtra(TEXT_COLOR, txtColor);
        if (type == "Typeface.DEFAULT")
            type = null;
        intent.putExtra(TEXT_TYPE, type);
        intent.putExtra(USER_TEXT, userText);
        intent.putExtra("Mode", editMode);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void openColorPicker(final int id) {
        int defaultColor;
        defaultColor = Color.WHITE;
        final ColorPickerDialog colorPickerDialog = new ColorPickerDialog(
                TextActivity.this, defaultColor, new OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                switch (id) {
                    case R.id.imgColorPicker:
                        txtPreviewText.setTextColor(color);
                        txtColor = color;
                        break;
                    default:
                        break;
                }
            }
        },
                new OnColorDeSelectedListener() {

                    @Override
                    public void onDeColorSelected() {

                    }
                });
        colorPickerDialog.setCanceledOnTouchOutside(false);
        colorPickerDialog.show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        Typeface tf = Typeface.createFromAsset(this.getAssets(),
                itemTypes[position]);
        txtPreviewText.setTypeface(tf);
        type = itemTypes[position];
        customListTextAdapter.setClickedChildPosition(position);
        customListTextAdapter.notifyDataSetChanged();
    }


    @Override
    public void onDialogPositiveClick(int code) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before,
                              int count) {

        txtPreviewText.setText(edtUsersText.getText().toString());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        userText = s.toString();
    }

}

