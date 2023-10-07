package lib.kalu.mediaplayer.widget.speed;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;

import lib.kalu.mediaplayer.R;

public class SpeedLinearLayout extends LinearLayout {
    public SpeedLinearLayout(Context context) {
        super(context);
    }

    public SpeedLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpeedLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SpeedLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        try {
            int h = ((View) getParent()).getMeasuredHeight();
            int minH = getResources().getDimensionPixelOffset(R.dimen.module_mediaplayer_dimen_speed_min_height);
            int minW = getResources().getDimensionPixelOffset(R.dimen.module_mediaplayer_dimen_speed_min_width);
            int height = h / 12;
            if (height < minH) {
                height = minH;
            }
            int width = height * 2;
            if (width < minW) {
                width = minW;
            }
            int measureSpecWidth = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
            int measureSpecHeight = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            setMeasuredDimension(measureSpecWidth, measureSpecHeight);
            requestLayout();
            if (null != getLayoutParams()) {
                int maxM = getResources().getDimensionPixelOffset(R.dimen.module_mediaplayer_dimen_speed_max_margin);
                int margin = h / 10;
                if (margin > maxM) {
                    margin = maxM;
                }
                ((RelativeLayout.LayoutParams) getLayoutParams()).topMargin = margin;
                ((RelativeLayout.LayoutParams) getLayoutParams()).rightMargin = margin;
            }
        } catch (Exception e) {
        }
    }
}
