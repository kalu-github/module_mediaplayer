//package lib.kalu.mediaplayer.widget.player;
//
//import android.content.Context;
//import android.os.Build;
//import android.util.AttributeSet;
//import android.widget.RelativeLayout;
//
//import androidx.annotation.Keep;
//import androidx.annotation.RequiresApi;
//
//import lib.kalu.mediaplayer.util.MPLogUtil;
//
//@Keep
//public class PlayerExtraLayout extends PlayerLayout {
//    public PlayerExtraLayout(Context context) {
//        super(context);
//    }
//
//    public PlayerExtraLayout(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public PlayerExtraLayout(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public PlayerExtraLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }
//
//    @Override
//    protected void init() {
//        cleanPlayerChangeListener();
//        try {
//            int childCount = getChildCount();
//            if (childCount > 0)
//                throw new Exception("childCount warning: " + childCount);
//            PlayerExtraView playerView = new PlayerExtraView(getContext());
//            playerView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
//            addView(playerView);
//        } catch (Exception e) {
//            MPLogUtil.log("PlayerExtraLayout => init => " + e.getMessage());
//        }
//    }
//}
