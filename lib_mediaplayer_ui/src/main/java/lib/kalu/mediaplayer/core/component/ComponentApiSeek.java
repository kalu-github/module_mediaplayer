package lib.kalu.mediaplayer.core.component;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import java.io.File;

import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerLayout;
import lib.kalu.mediaplayer.widget.player.PlayerView;

public interface ComponentApiSeek extends ComponentApi {

    default SeekBar findSeekBar() {
        return null;
    }

}