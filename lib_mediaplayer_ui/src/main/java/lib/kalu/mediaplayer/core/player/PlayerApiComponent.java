package lib.kalu.mediaplayer.core.player;

import android.annotation.SuppressLint;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

interface PlayerApiComponent extends PlayerApiBase {

    default void clearAllComponent() {
        try {
            ViewGroup viewGroup = getBaseControlViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                ((ComponentApi) childAt).attachPlayerApi(null);
            }
            viewGroup.removeAllViews();
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiComponent => clearAllComponent => " + e.getMessage());
        }
    }

    default void clearComponent(java.lang.Class<?> cls) {
        try {
            LinkedList<View> views = new LinkedList<>();
            ViewGroup viewGroup = getBaseControlViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                if (childAt.getClass() == cls) {
                    ((ComponentApi) childAt).attachPlayerApi(null);
                    views.add(childAt);
                }
            }
            for (View v : views) {
                viewGroup.removeView(v);
            }
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiComponent => clearComponent => " + e.getMessage());
        }
    }

    @SuppressLint("StaticFieldLeak")
    default void addComponent(ComponentApi componentApi) {
        try {
            if (null == componentApi)
                throw new Exception("componentApi error: null");
            ViewGroup viewGroup = getBaseControlViewGroup();
            if (null == viewGroup)
                throw new Exception("viewGroup error: null");
            viewGroup.addView((View) componentApi, 0, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiComponent => addComponent => " + e.getMessage());
        }
    }

    default void addAllComponent(List<ComponentApi> componentApis) {
        try {
            if (null == componentApis || componentApis.size() <= 0)
                throw new Exception("componentApi error: null");
            ViewGroup viewGroup = getBaseControlViewGroup();
            if (null == viewGroup)
                throw new Exception("viewGroup error: null");
            viewGroup.removeAllViews();
            for (ComponentApi componentApi : componentApis) {
                if (null == componentApi)
                    continue;
                addComponent(componentApi);
            }
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiComponent => addAllComponent => " + e.getMessage());
        }
    }

    default <T extends ComponentApi> T findComponent(java.lang.Class<?> cls) {
        try {
            ViewGroup viewGroup = getBaseControlViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                if (childAt.getClass() == cls) {
                    return (T) childAt;
                }
            }
            throw new Exception("not find");
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiComponent => callWindowEvent => " + e.getMessage());
            return null;
        }
    }

    default void attachPlayerApi(PlayerApi playerApi) {
        try {
            ViewGroup viewGroup = getBaseControlViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                ((ComponentApi) childAt).attachPlayerApi(playerApi);
            }
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiComponent => attachPlayerKernel => " + e.getMessage());
        }
    }


    default void dispatchKeyEventComponents(@NonNull KeyEvent event) {
        try {
            ViewGroup viewGroup = getBaseControlViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                ((ComponentApi) childAt).dispatchKeyEventComponent(event);
            }
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiComponent => dispatchKeyEventComponents => " + e.getMessage());
        }
    }

    default void callUpdateTimeMillis(long seek, long position, long duration) {
        try {
            ViewGroup viewGroup = getBaseControlViewGroup();
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("not find component");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                if (null == childAt)
                    continue;
                if (!(childAt instanceof ComponentApi))
                    continue;
                ((ComponentApi) childAt).onUpdateTimeMillis(seek, position, duration);
            }
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiComponent => callUpdateTimeMillis => " + e.getMessage());
        }
    }
}
