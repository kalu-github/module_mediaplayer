package lib.kalu.mediaplayer.widget.loading;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

@SuppressLint("AppCompatCustomView")

public class MPLoadingViewSpeed extends MPLoadingView {

    public MPLoadingViewSpeed(Context context) {
        super(context);
    }

    public MPLoadingViewSpeed(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MPLoadingViewSpeed(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MPLoadingViewSpeed(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        try {
            int h = ((View) getParent()).getMeasuredHeight();
            int height = (int) (h * 0.8);
            int makeMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            setMeasuredDimension(makeMeasureSpec, makeMeasureSpec);
        } catch (Exception e) {
        }
    }
}
