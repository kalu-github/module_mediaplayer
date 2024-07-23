package lib.kalu.mediaplayer.widget.radio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.DrawableRes;

import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;

public final class RadioButton extends android.widget.RadioButton {

    @PlayerType.EpisodeFlagLoactionType.Value
    private int mLocation = PlayerType.EpisodeFlagLoactionType.DEFAULT;
    @DrawableRes
    private int mDrawableVip = 0;
    private Bitmap mBitmapVip = null;
    @DrawableRes
    private int mDrawableFree = 0;
    private Bitmap mBitmapFree = null;

    public RadioButton(Context context) {
        super(context);
    }

    public RadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    public RadioButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setFlag(@DrawableRes int vip, @DrawableRes int free) {
        this.mDrawableVip = vip;
        this.mDrawableFree = free;
    }

    public void setFlag(@PlayerType.EpisodeFlagLoactionType.Value int location, @DrawableRes int vip, @DrawableRes int free) {
        this.mLocation = location;
        this.mDrawableVip = vip;
        this.mDrawableFree = free;
        if (null != mBitmapFree) {
            mBitmapFree.recycle();
            mBitmapFree = null;
        }
        if (null != mBitmapVip) {
            mBitmapVip.recycle();
            mBitmapVip = null;
        }
    }

    public void clearFlag() {
        this.mLocation = PlayerType.EpisodeFlagLoactionType.DEFAULT;
        this.mDrawableVip = 0;
        this.mDrawableFree = 0;
        if (null != mBitmapFree) {
            mBitmapFree.recycle();
            mBitmapFree = null;
        }
        if (null != mBitmapVip) {
            mBitmapVip.recycle();
            mBitmapVip = null;
        }
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != View.VISIBLE) {
            if (null != mBitmapFree) {
                mBitmapFree.recycle();
                mBitmapFree = null;
            }
            if (null != mBitmapVip) {
                mBitmapVip.recycle();
                mBitmapVip = null;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 免费图标
        try {
            if (mDrawableFree == 0)
                throw new Exception("error: mDrawableFree == 0");
            if (null == mBitmapFree) {
                mBitmapFree = BitmapFactory.decodeResource(getResources(), mDrawableFree);
            }
            if (null == mBitmapFree)
                throw new Exception("error: mBitmapFree null");

            int width = mBitmapFree.getWidth();
            int height = mBitmapFree.getHeight();
            float value = (float) width / height;

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int bottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10 * metrics.density, metrics);
            if (mLocation == PlayerType.EpisodeFlagLoactionType.LEFT_TOP) {
                int right = (int) (bottom * value);
                Rect rect = new Rect(0, 0, right, bottom);
                canvas.drawBitmap(mBitmapFree, null, rect, null);
            } else if (mLocation == PlayerType.EpisodeFlagLoactionType.RIGHT_TOP) {
                int w = getWidth();
                int right = (int) (bottom * value);
                int left = w - right;
                Rect rect = new Rect(left, 0, w, bottom);
                canvas.drawBitmap(mBitmapFree, null, rect, null);
            }
        } catch (Exception e) {
            LogUtil.log("RadioButton => onDraw => Exception1 " + e.getMessage());
        }

        // 收费图标
        try {
            if (mDrawableVip == 0)
                throw new Exception("error: mDrawableVip == 0");
            if (null == mBitmapVip) {
                mBitmapVip = BitmapFactory.decodeResource(getResources(), mDrawableVip);
            }
            if (null == mBitmapVip)
                throw new Exception("error: mBitmapVip null");

            int width = mBitmapVip.getWidth();
            int height = mBitmapVip.getHeight();
            float value = (float) width / height;

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int bottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10 * metrics.density, metrics);
            if (mLocation == PlayerType.EpisodeFlagLoactionType.LEFT_TOP) {
                int right = (int) (bottom * value);
                Rect rect = new Rect(0, 0, right, bottom);
                canvas.drawBitmap(mBitmapVip, null, rect, null);
            } else if (mLocation == PlayerType.EpisodeFlagLoactionType.RIGHT_TOP) {
                int w = getWidth();
                int right = (int) (bottom * value);
                int left = w - right;
                Rect rect = new Rect(left, 0, w, bottom);
                canvas.drawBitmap(mBitmapVip, null, rect, null);
            }

        } catch (Exception e) {
            LogUtil.log("RadioButton => onDraw => Exception2 " + e.getMessage());
        }
    }
}
