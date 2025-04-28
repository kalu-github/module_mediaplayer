package com.kalu.mediaplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;

@SuppressLint("AppCompatCustomView")
public class RadioButton extends android.widget.RadioButton {
    public RadioButton(Context context) {
        super(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            CharSequence text = getText();
            float measureText = getPaint().measureText(String.valueOf(text));
            int spec = MeasureSpec.makeMeasureSpec((int) measureText, MeasureSpec.EXACTLY);
            super.onMeasure(spec, heightMeasureSpec);
        } catch (Exception e) {
        }
    }
}
