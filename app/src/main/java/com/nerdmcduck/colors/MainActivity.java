package com.nerdmcduck.colors;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.slider.Slider;

import org.jetbrains.annotations.NotNull;

import java.util.Random;

/*
TODO:
  Redesign app layout using Material Design
  Redesign app icon
  Adjust complementary color to make it easier to see for certain colors
 */

public class MainActivity extends AppCompatActivity {

    protected View screenView;
    protected Button hide_content_btn;
    protected TextView tvNerdmcduck, tvHexStr, tvhexColorCode, tvRGBLabel, tvRGBCode;
    protected Slider sliderHue, sliderSat, sliderBright;
    protected EditText etHue, etSat, etBrightness;
    private float[] commonHSV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //Initialize the View by getting the id of the parent layout
        screenView = findViewById(R.id.rView);
        hide_content_btn = findViewById(R.id.hide_content_btn);
        //tvNerdmcduck = findViewById(R.id.nerdmcduck);
        tvHexStr = findViewById(R.id.hexString);
        tvhexColorCode = findViewById(R.id.hexColorCode);
        tvRGBLabel = findViewById(R.id.rgbCodeLabel);
        tvRGBCode = findViewById(R.id.rgbColorCode);
        sliderHue = findViewById(R.id.sliderHue);
        sliderSat = findViewById(R.id.sliderSaturation);
        sliderBright = findViewById(R.id.sliderBrightness);
        etHue = findViewById(R.id.editTextHue);
        etSat = findViewById(R.id.editTextSaturation);
        etBrightness = findViewById(R.id.editTextBrightness);

        //Retrieve previously saved values if this is not the first time calling onCreate()
        if (savedInstanceState != null) {
            Log.d("savedInstanceState", "savedInstanceState not null");
            CharSequence hexString = savedInstanceState.getCharSequence("hexString");
            int bgColor = savedInstanceState.getInt("screenViewColor");

            if (bgColor == Color.WHITE || bgColor == Color.BLACK) {
                setStartingBGColor(screenView);
            } else {
                screenView.setBackgroundColor(bgColor);
            }

            tvHexStr.setText(hexString);
            setHTMLText(tvHexStr, bgColor);
            tvRGBCode.setText(savedInstanceState.getCharSequence("rgbCode"));
            commonHSV = savedInstanceState.getFloatArray("hsv");
            sliderHue.setValue(savedInstanceState.getFloat("sliderHueValue"));
            sliderSat.setValue(savedInstanceState.getFloat("sliderSatValue"));
            sliderBright.setValue(savedInstanceState.getFloat("sliderBrightValue"));
            setTextViewColors();

            //Visibility
            hide_content_btn.setText(savedInstanceState.getCharSequence("HideContentBtnState"));
            int visibilityState = savedInstanceState.getInt("VisibilityState");
            tvHexStr.setVisibility(visibilityState);
            tvRGBLabel.setVisibility(visibilityState);
            tvhexColorCode.setVisibility(visibilityState);
            tvRGBCode.setVisibility(visibilityState);

        } else {
            Log.d("savedInstanceState", "savedInstanceState is null");
            //Initialize variables and set starting values
            init();
        }

        /*
         *  Sets the screen to black when its touched
         * @params v - The View being passed in
         * @params event - The MotionEvent generated by this View
         */
        screenView.setOnTouchListener((v, event) -> { //non-lamda exp: screenView.setOnTouchListener(new onTouchListener(View v, MotionEvent event))
            // Context appContext = getApplicationContext();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    int bgColor = getRandomHSVColor();
                    screenView.setBackgroundColor(bgColor);
                    setHTMLText(tvHexStr, bgColor);
                    tvRGBCode.setText(getRGB(tvHexStr.getText()));
                    setTextViewColors();
                    commonHSV = getHSV(bgColor);
                    updateSliderValues();

                    Log.d("screenView", "Screen tapped");
                    break;
                case MotionEvent.ACTION_UP:
                    v.performClick();
                    Log.d("screenView", "Screen released");
                    break;
                default:
                    break;
            }
            return true;
        });

        //Change hue slider
        sliderHue.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                // Responds to when slider's touch event is being started
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                // Responds to when slider's touch event is being stopped
                Log.d("slider", "Slider end value: " + slider.getValue());
                commonHSV = getHSV(getViewColorProperties(screenView).getColor());
            }
        });

        // Responds to when the Hue slider's value is being changed
        sliderHue.addOnChangeListener((slider, value, fromUser) -> {

            //Log.d("slider", slider.toString().substring(slider.toString().indexOf("app")) + " updating: " + value);

            int bgColor = hsvToColor(value, commonHSV[1], commonHSV[2]);
            screenView.setBackgroundColor(bgColor);
            setHTMLText(tvHexStr, bgColor);
            tvRGBCode.setText(getRGB(tvHexStr.getText()));
            setTextViewColors();
            etHue.setText(String.format(Integer.toString((int) value), "%d"), TextView.BufferType.EDITABLE);


            Log.d("slider", "text direction: " + etHue.getTextDirection());

        });

        //Map Hue edit text to change the Hue slider's value when the text is directly changed
        etHue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.d("slider", "Hue edit beforeTextChanged called");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.d("slider", "Hue edit onTextChanged called");
                if (s.length() != 0) {
                    etHue.setSelection(s.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Log.d("slider", "Hue edit afterTextChanged called");

                if (s.length() != 0) {

                    if (Float.parseFloat(s.toString()) > 360) {
                        Toast.makeText(getApplicationContext(), "Value should be less than 360", Toast.LENGTH_LONG).show();
                        etHue.setText("");
                    } else {
                        sliderHue.setValue(Float.parseFloat(s.toString()));
                    }
                }

            }
        });

        //Change the Saturation slider
        sliderSat.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                // Responds to when slider's touch event is being started
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                // Responds to when slider's touch event is being stopped
                commonHSV = getHSV(getViewColorProperties(screenView).getColor());
            }
        });
        // Responds to when slider's value is being changed
        sliderSat.addOnChangeListener((slider, value, fromUser) -> {

            Log.d("slider", slider.toString().substring(slider.toString().indexOf("app")) + " updating: " + value);
            int bgColor = hsvToColor(commonHSV[0], value / 100.0f, commonHSV[2]);
            screenView.setBackgroundColor(bgColor);
            setHTMLText(tvHexStr, bgColor);
            tvRGBCode.setText(getRGB(tvHexStr.getText()));
            setTextViewColors();
            etSat.setText(String.format(Integer.toString((int) value), "%d"), TextView.BufferType.EDITABLE);

        });

        //Map Saturation edit text to change the Saturation slider's value when the text is directly changed
        etSat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.d("slider", "Hue edit beforeTextChanged called");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.d("slider", "Hue edit onTextChanged called");
                if (s.length() != 0) {
                    etSat.setSelection(s.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Log.d("slider", "Hue edit afterTextChanged called");

                if (s.length() != 0) {

                    if (Float.parseFloat(s.toString()) > 100) {
                        Toast.makeText(getApplicationContext(), "Value should be less than 100", Toast.LENGTH_LONG).show();
                        etSat.setText("");
                    } else {
                        sliderSat.setValue(Float.parseFloat(s.toString()));
                    }
                }

            }
        });

        //Change the Brightness slider
        sliderBright.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
                // Responds to when slider's touch event is being started
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                // Responds to when slider's touch event is being stopped
                commonHSV = getHSV(getViewColorProperties(screenView).getColor());
            }
        });
        // Responds to when slider's value is being changed
        sliderBright.addOnChangeListener((slider, value, fromUser) -> {

            Log.d("slider", slider.toString().substring(slider.toString().indexOf("app")) + " updating: " + value);
            int bgColor = hsvToColor(commonHSV[0], commonHSV[1], value / 100.0f);
            screenView.setBackgroundColor(bgColor);

            setHTMLText(tvHexStr, bgColor);
            tvRGBCode.setText(getRGB(tvHexStr.getText()));
            setTextViewColors();
            etBrightness.setText(String.format(Integer.toString((int) value), "%d"), TextView.BufferType.EDITABLE);

        });

        //Map Brightness edit text to change the Brightness slider's value when the text is directly changed
        etBrightness.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.d("slider", "Hue edit beforeTextChanged called");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.d("slider", "Hue edit onTextChanged called");
                if (s.length() != 0) {
                    etBrightness.setSelection(s.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Log.d("slider", "Hue edit afterTextChanged called");

                if (s.length() != 0) {

                    if (Float.parseFloat(s.toString()) > 100) {
                        Toast.makeText(getApplicationContext(), "Value should be less than 100", Toast.LENGTH_LONG).show();
                        etBrightness.setText("");
                    } else {
                        sliderBright.setValue(Float.parseFloat(s.toString()));
                    }
                }

            }
        });

        //add listener to button to hide Hex and RGB codes when clicked
        hide_content_btn.setOnClickListener((view) -> {
            if (hide_content_btn.getText().equals(getResources().getString(R.string.hide))) {
                hide_content_btn.setText(R.string.show);
                tvHexStr.setVisibility(View.INVISIBLE);
                tvRGBLabel.setVisibility(View.INVISIBLE);
                tvhexColorCode.setVisibility(View.INVISIBLE);
                tvRGBCode.setVisibility(View.INVISIBLE);
            } else {
                hide_content_btn.setText(R.string.hide);
                tvHexStr.setVisibility(View.VISIBLE);
                tvRGBLabel.setVisibility(View.VISIBLE);
                tvhexColorCode.setVisibility(View.VISIBLE);
                tvRGBCode.setVisibility(View.VISIBLE);
            }
        });

    }


    /**
     * Return the hsv values for the specified color
     *
     * @param color - The color to convert to HSV format
     * @return HSV float array, where
     * hsv[0] = Hue  from 0..360,
     * hsv[1] = Saturation from 0..1
     * hsv[2] = Brightness from 0..1
     */
    private float[] getHSV(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return hsv;
    }

    //takes a text view and color as input, converts it to a url and sets it to the Textview
    private void setHTMLText(TextView tv, int color) {
        // String convertedHexCode = getApplicationContext().getString(R.string.encycolorpedia).concat(convertToHex(color).substring(1));
        //<a href=https://encycolorpedia.com/hexCode>hexCode </a>
        String hexCode = convertToHex(color);
        StringBuilder sb = new StringBuilder();
        sb.append("<a href='")
                .append(getApplicationContext().getString(R.string.encycolorpedia))
                .append(hexCode.substring(1)) //Remove # at the beginning
                .append("' rel='external' target='_top'")
                .append(">")
                .append(hexCode)
                .append("</a>");

        Log.d("htmlText", sb.toString() + " clickable: " + tv.isClickable());

        //Need to this check since Html.fromHtml(String source) is deprecated after Android N
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv.setText(Html.fromHtml(sb.toString(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            tv.setText(Html.fromHtml(sb.toString()));
        }

        if (!tv.isClickable()) {
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            tv.setLinksClickable(true);
        }

    }

    /**
     * Whenever Android destroys and recreates the Activity for orientation change,
     * it calls onSaveInstanceState() before destroying and calls onCreate() after recreating.
     * Anything saved in the bundle in onSaveInstanceState, can be retrieved from the onCreate() parameter.
     *
     * @param outState the bundle that will keep our variables
     */
    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("hexString", tvHexStr.getText());
        outState.putCharSequence("rgbCode", tvRGBCode.getText());
        outState.putInt("screenViewColor", getViewColorProperties(screenView).getColor());
        outState.putFloatArray("hsv", commonHSV);
        outState.putFloat("sliderHueValue", sliderHue.getValue());
        outState.putFloat("sliderSatValue", sliderSat.getValue());
        outState.putFloat("sliderBrightValue", sliderBright.getValue());
        outState.putCharSequence("HideContentBtnState", hide_content_btn.getText());
        outState.putInt("VisibilityState", tvHexStr.getVisibility());

    }

    //Initializes the starting values
    private void init() {
        setStartingBGColor(screenView);
        tvHexStr.setText(convertToHex(getViewColorProperties(screenView).getColor()));
        tvHexStr.setMovementMethod(LinkMovementMethod.getInstance());
        tvHexStr.setLinksClickable(true);
        setHTMLText(tvHexStr, getViewColorProperties(screenView).getColor());
        tvRGBCode.setText(getRGB(tvHexStr.getText()));
        commonHSV = getHSV(getViewColorProperties(screenView).getColor());
        setTextViewColors();
        updateSliderValues();
        // Log.d("commonHSV", "Hue: "+commonHSV[0] + " , saturation: " + commonHSV[1] + " brightness: " + commonHSV[2] );
    }

    /**
     * Sets the color for all TextView objects that share the same color
     */
    private void setTextViewColors() {
        setComplimentaryColor(screenView, tvHexStr);
        int complimentaryColor = tvHexStr.getCurrentTextColor();
        // tvNerdmcduck.setTextColor(complimentaryColor);
        tvhexColorCode.setTextColor(complimentaryColor);
        tvRGBCode.setTextColor(complimentaryColor);
        tvRGBLabel.setTextColor(complimentaryColor);
    }

    /**
     * Set the slider values to the current background color
     */
    private void updateSliderValues() {
        sliderHue.setValue(commonHSV[0]);
        sliderHue.performClick();
        sliderSat.setValue(commonHSV[1] * 100.0f);
        sliderSat.performClick();
        sliderBright.setValue(commonHSV[2] * 100.0f);
        sliderBright.performClick();
    }


    /**
     * Give a color, return a formatted string in the form of (R,G,B)
     *
     * @param hexColor - The color in a hex string in the form of #FFFFFF
     * @return A string containing the equivalent RGB values in the form (R,G,B)
     */
    private String getRGB(CharSequence hexColor) {
        int red = Integer.decode(hexColor.subSequence(0, 3).toString());
        int green = Integer.decode("#".concat(hexColor.subSequence(3, 5).toString()));
        int blue = Integer.decode("#".concat(hexColor.subSequence(5, hexColor.length()).toString()));

        StringBuilder rgb = new StringBuilder();
        rgb.append("(")
                .append(red)
                .append(",")
                .append(green)
                .append(",")
                .append(blue)
                .append(")");
        return rgb.toString();
    }

    //Given a color, return a formatted hex string, without the Alpha value
    private String convertToHex(int color) {
        return getApplicationContext().getString(R.string.hex_str)
                .concat(Integer.toHexString(color).toUpperCase().substring(2));
    }

    /**
     * Get the given view's Background color properties
     *
     * @param view - The view's which to retrieve the background color from
     * @return ColorDrawable object from the given View
     */
    private ColorDrawable getViewColorProperties(View view) {
        return (ColorDrawable) view.getBackground();
    }

    /**
     * Set the background's complimentary color to the TextVIew
     * by inverting the background color's rgb values while leaving the Alpha value as is.
     *
     * @param view - The View form which you want to take the color from.
     * @param tv   - The Textview for which you want to change the color to.
     *             As TextView is pass-by-reference, we do not need to return a value.
     */
    private void setComplimentaryColor(View view, TextView tv) {
        Log.d("compColor", "View: " + view.getBackground());
        tv.setTextColor(getViewColorProperties(view).getColor() ^ 0x00ffffff);
        if (tv.isClickable()) {
            Log.d("htmlText", "In setComplimentaryColor. " + tv.getText() + " is Clickable");
            tv.setLinkTextColor(getViewColorProperties(view).getColor() ^ 0x00ffffff);
            return;
        }
        Log.d("htmlText", "In setComplimentaryColor. " + tv.getText() + " is not Clickable");

    }

    //Check if Night Mode is enabled and set background accordingly
    private void setStartingBGColor(View screenView) {
        Log.i("nightmode", "Retrieving night mode info");
        int nightModeFlags =
                screenView.getContext().getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                //Start with a black background
                screenView.setBackgroundColor(hsvToColor(0f, 0f, 0f));
                //Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.toast_msg), Toast.LENGTH_LONG).show();
                break;

            case Configuration.UI_MODE_NIGHT_NO:
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                //Start with a white background
                screenView.setBackgroundColor(hsvToColor(0f, 0f, 1f));
                break;
        }
    }

    //convert HSV components to corresponding ARGB Colors with Alpha set to 0xFF
    protected int hsvToColor(float hue, float saturation, float brightness) {
        //Log.d("hsv", "Successfully called");
        float[] hsv = new float[3];
        hsv[0] = hue;
        hsv[1] = saturation;
        hsv[2] = brightness;
        return Color.HSVToColor(hsv);
    }

    //convert randomly chosen HSV components to corresponding ARGB Colors with A set to 0xFF
    protected int getRandomHSVColor() {
        float[] hsv = new float[3];
        Random rand = new Random();
        hsv[0] = rand.nextFloat() * 360f; //hue
        hsv[1] = rand.nextFloat(); //Saturation
        hsv[2] = rand.nextFloat(); //value or brightness
        return Color.HSVToColor(hsv);
    }


}