package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.bean.info.MenuInfo;
import lib.kalu.mediaplayer.bean.type.PlayerType;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.util.LogUtil;

public class ComponentMenu extends RelativeLayout implements ComponentApi {

    private int curSeleceed = 1; // 默认第二集
    private int TYPE_EPISODE = 1000;
    private int TYPE_SCALE = 2000;
    private int TYPE_SPEED = 3000;

    public ComponentMenu(Context context) {
        super(context);
        inflate();
    }

    @Override
    public int initLayoutId() {
        return R.layout.module_mediaplayer_component_menu;
    }

    @Override
    public int initViewIdRoot() {
        return R.id.module_mediaplayer_component_menu_root;
    }

    @Override
    public void onUpdateProgress(boolean isFromUser, long trySeeDuration, long position, long duration) {

        if (isFromUser && trySeeDuration == -1 && position == -1 && duration == -1) {
            long millis = System.currentTimeMillis();
            setTag(millis);
        } else if (!isFromUser) {
            try {
                long timeMillis = (long) getTag();
                if (timeMillis <= 0L)
                    throw new Exception();
                long currentTimeMillis = System.currentTimeMillis();
                long cast = currentTimeMillis - timeMillis;
                if (cast <= 4000L)
                    throw new Exception();
                hide();
            } catch (Exception e) {
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        LogUtil.log("ComponentMenu => dispatchKeyEvent => action =  " + event.getAction() + ", keyCode = " + event.getKeyCode() + ", repeatCount = " + event.getRepeatCount());

        // keycode_dpad_center
        if (event.getAction() == KeyEvent.ACTION_DOWN && (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER || event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
            try {
                View focus = findFocus();
                if (null == focus)
                    throw new Exception("warning: focus null");
                int focusId = focus.getId();
                if (focusId != R.id.module_mediaplayer_component_menu_item_content)
                    throw new Exception("warning: focusId != R.id.module_mediaplayer_component_menu_item_content");
                Object _tag = focus.getTag();
                if (null == _tag)
                    throw new Exception("warning: _tag null");
                int _type = ((int[]) _tag)[0];

                // 倍速
                if (_type == TYPE_SPEED) {
                    //
                    onUpdateProgress(true, -1, -1, -1);
                    //
                    setVideoSpeed(((int[]) _tag)[1]);
//                    hide();
                }
                // 画面比例
                else if (_type == TYPE_SCALE) {
                    //
                    onUpdateProgress(true, -1, -1, -1);
                    //
                    setVideoScaleType(((int[]) _tag)[1]);
//                    hide();
                }
                // 选集
                else if (_type == TYPE_EPISODE) {
                    //
                    int select = ((int[]) _tag)[2];
                    curSeleceed = select;
                    ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_content);
                    int childCount = viewGroup.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = viewGroup.getChildAt(i);
                        Object tag = childAt.getTag();
                        int cur = ((int[]) tag)[2];
                        ((int[]) tag)[1] = select;
                        childAt.setSelected(cur == select);
                        // childAt.setActivated(i == indexedOfChild);
                    }
                    //
                    OnPlayerEpisodeListener listener = getPlayerView().getOnPlayerEpisodeListener();
                    if (null != listener) {
                        listener.onEpisode(select);
                    }
                    //
                    hide();
                }
                return true;
            } catch (Exception e) {
                LogUtil.log("ComponentMenu => keycodeCenter => keycode_dpad_center => " + e.getMessage());
            }

            boolean componentShowing = isComponentShowing();
            if (componentShowing)
                return true;
        }
        // action_down keycode_dpad_down
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
            try {
                boolean componentShowing = isComponentShowing();
                if (!componentShowing) {
                    show();
                    superCallEvent(false, true, PlayerType.EventType.COMPONENT_MENU_SHOW);
                    showTabGroup(0);
                }
                //
                ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
                int childCount = viewGroup.getChildCount();
                if (childCount <= 0)
                    throw new Exception("warning: childCount <= 0");
                for (int i = 0; i < childCount; i++) {
                    View childAt = viewGroup.getChildAt(i);
                    boolean selected = childAt.isSelected();
                    if (!selected)
                        continue;
                    childAt.requestFocus();
                }
                //
                onUpdateProgress(true, -1, -1, -1);
                return true;
            } catch (Exception e) {
            }
        }
        // action_down -> keycode_dpad_up
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
            try {
                View focus = findFocus();
                if (null == focus)
                    throw new Exception("warning: focus null");
                int focusId = focus.getId();
                if (focusId != R.id.module_mediaplayer_component_menu_tab_group_item)
                    throw new Exception("warning: focusId != R.id.module_mediaplayer_component_menu_tab_group_item");
                ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_content);
                int speedCount = viewGroup.getChildCount();
                for (int i = 0; i < speedCount; i++) {
                    View childAt = viewGroup.getChildAt(i);
                    if (null == childAt)
                        continue;
                    boolean selected = childAt.isSelected();
                    if (!selected)
                        continue;
                    childAt.requestFocus();
                    //
                    onUpdateProgress(true, -1, -1, -1);
                    return true;
                }
            } catch (Exception e) {
                LogUtil.log("ComponentMenu => keycodeUp => Exception " + e.getMessage());
            }
        }
        // action_down -> keycode_dpad_left
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {

            // updateTimeMillis();
            View focus = findFocus();
            int focusId = focus.getId();

            // 菜单
            if (focusId == R.id.module_mediaplayer_component_menu_tab_group_item) {
                ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
                int indexOfChild = viewGroup.indexOfChild(focus);
                if (indexOfChild <= 0) {
                    //
                    onUpdateProgress(true, -1, -1, -1);
                    return true;
                } else {
//                        selectedTabGroup(--indexOfChild);
//                        requestFocusedTabGroup();
                    //
                    onUpdateProgress(true, -1, -1, -1);
                    //
                    showTabGroup(--indexOfChild);
                    return viewGroup.dispatchKeyEvent(event);
                }
            }
            // 内容
            else {
                ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_content);
                int indexOfChild = viewGroup.indexOfChild(focus);
                LogUtil.log("ComponentMenu => keycodeUp => indexOfChild = " + indexOfChild);
                if (indexOfChild <= 0) {
                    try {
                        Object _tag = focus.getTag();
                        if (!(_tag instanceof int[]))
                            throw new Exception("warning: _tag not instanceof int[]");
                        int type = ((int[]) _tag)[0];
                        if (type != TYPE_EPISODE)
                            throw new Exception("warning: type != TYPE_EPISODE");
                        int _cur = ((int[]) _tag)[2];
                        LogUtil.log("ComponentMenu => keycodeUp => _cur = " + _cur);
                        if (_cur <= 0)
                            throw new Exception("warning: _cur <= 0");
                        for (int i = 0; i < 10; i++) {
                            //
                            TextView childAt = (TextView) viewGroup.getChildAt(i);
                            int[] tag = (int[]) childAt.getTag();
                            int cur = tag[2];
                            int select = tag[1];
                            int newCur = cur - 1;
                            LogUtil.log("ComponentMenu => keycodeUp => i = " + i + ", select = " + select + ", newCur = " + newCur);
                            tag[2] = newCur;

                            //
                            childAt.setSelected(select == newCur);
                            childAt.setText(String.valueOf(newCur + 1));
                        }
                    } catch (Exception e) {
                    }
                    //
                    onUpdateProgress(true, -1, -1, -1);
                    return true;
                } else {
                    //
                    onUpdateProgress(true, -1, -1, -1);
                    return viewGroup.dispatchKeyEvent(event);
                }
            }
        }
        // action_down -> keycode_dpad_right
        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
            // return keycodeRight(KeyEvent.ACTION_DOWN);

            View focus = findFocus();
            int focusId = focus.getId();

            // 菜单
            if (focusId == R.id.module_mediaplayer_component_menu_tab_group_item) {
                ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
                int childCount = viewGroup.getChildCount();
                int indexOfChild = viewGroup.indexOfChild(focus);
                if (indexOfChild + 1 >= childCount) {

                    //
                    onUpdateProgress(true, -1, -1, -1);

                    return true;
                } else {

                    //
                    onUpdateProgress(true, -1, -1, -1);

                    showTabGroup(++indexOfChild);
                    return viewGroup.dispatchKeyEvent(event);
                }
            }
            // 内容
            else {
                ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_content);
                int childCount = viewGroup.getChildCount();
                int indexOfChild = viewGroup.indexOfChild(focus);
                if (indexOfChild + 1 >= childCount) {

                    try {
                        Object _tag = focus.getTag();
                        if (!(_tag instanceof int[]))
                            throw new Exception("warning: _tag not instanceof int[]");
                        int type = ((int[]) _tag)[0];
                        if (type != TYPE_EPISODE)
                            throw new Exception("warning: type != TYPE_EPISODE");
                        int _cur = ((int[]) _tag)[2];
                        LogUtil.log("ComponentMenu => keycodeUp => _cur = " + _cur);
                        int _length = ((int[]) _tag)[3];
                        LogUtil.log("ComponentMenu => keycodeUp => _length = " + _length);
                        if (_cur + 1 >= _length)
                            throw new Exception("warning: _cur + 1 > _length");
                        for (int i = 0; i < 10; i++) {
                            //
                            TextView childAt = (TextView) viewGroup.getChildAt(i);
                            int[] tag = (int[]) childAt.getTag();
                            int cur = tag[2];
                            int select = tag[1];
                            int newCur = cur + 1;
                            LogUtil.log("ComponentMenu => keycodeUp => i = " + i + ", select = " + select + ", newCur = " + newCur);
                            tag[2] = newCur;

                            //
                            childAt.setSelected(select == newCur);
                            childAt.setText(String.valueOf(newCur + 1));
                        }
                    } catch (Exception e) {

                    }

                    //
                    onUpdateProgress(true, -1, -1, -1);

                    return true;
                } else {

                    // selectedTabGroup(--indexOfChild);
                    //  requestFocusedTabGroup();

                    //
                    onUpdateProgress(true, -1, -1, -1);

                    return viewGroup.dispatchKeyEvent(event);
                }
            }
        }

        return false;
    }

    private void showTabGroup(int index) {

        // 填充数据
        try {
            if (index < 0)
                throw new Exception("warning: index < 0");
            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
            int childCount = viewGroup.getChildCount();
            if (childCount > 0)
                throw new Exception("warning: childCount > 0");
            List<MenuInfo> list = initData();
            if (null != list && !list.isEmpty()) {
                for (int i = 0; i < list.size(); i++) {
                    LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu_item_tab, viewGroup, true);
                    View childAt = viewGroup.getChildAt(i);
                    if (null == childAt)
                        continue;
                    ((TextView) childAt).setText(list.get(i).getName());
                }
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => showTabGroup => Exception1 " + e.getMessage());
        }

        // 选中
        try {
            if (index < 0)
                throw new Exception("warning: index < 0");
            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_group);
            int childCount = viewGroup.getChildCount();
            if (childCount <= 0)
                throw new Exception("warning: childCount <= 0");
            if (index >= childCount)
                throw new Exception("warning: index >= childCount");
            for (int i = 0; i < childCount; i++) {
                View childAt = viewGroup.getChildAt(i);
                childAt.setSelected(i == index);
            }
        } catch (Exception e) {
            LogUtil.log("ComponentMenu => showTabGroup => Exception2 " + e.getMessage());
        }

        // 内容
        try {
            ViewGroup viewGroup = findViewById(R.id.module_mediaplayer_component_menu_tab_content);
            viewGroup.removeAllViews();
            //
            if (index < 0)
                throw new Exception("error: index <0");
            List<MenuInfo> list = initData();
            if (null == list)
                throw new Exception("error: list null");
            int size = list.size();
            if (index >= size)
                throw new Exception("error: index >= size");

            MenuInfo menuInfo = list.get(index);
            int episodeLength = menuInfo.getEpisodeLength();

            // 选集
            if (episodeLength > 0) {

                int num = curSeleceed / 10;
                int start = num * 10;
                int end = start + 10;
                if (end > episodeLength) {
                    int cast = end - episodeLength;
                    start = start - cast;
                }

                for (int i = 0; i < 10; i++) {
                    //
                    LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu_item_content, viewGroup, true);
                    TextView childAt = (TextView) viewGroup.getChildAt(i);
                    //
                    childAt.setVisibility(i >= episodeLength ? View.INVISIBLE : View.VISIBLE);
                    if (i >= episodeLength)
                        continue;
                    int cur = i + start;
                    //
                    childAt.setTag(new int[]{TYPE_EPISODE, curSeleceed, cur, episodeLength});
                    childAt.setSelected(cur == curSeleceed);
                    childAt.setText(String.valueOf(cur + 1));
                }
            }
            // 其他
            else {
                int[] contentData = menuInfo.getData();
                if (null == contentData)
                    throw new Exception("error: contentData null");
                if (contentData.length == 0)
                    throw new Exception("error: contentData.length == 0");

                int scaleType = getVideoScale();
                int speedType = getVideoSpeed();

                List<Integer> listScale = Arrays.asList(PlayerType.ScaleType._1_1,
                        PlayerType.ScaleType._4_3,
                        PlayerType.ScaleType._5_4,
                        PlayerType.ScaleType._16_10,
                        PlayerType.ScaleType._16_9,
                        PlayerType.ScaleType.REAL,
                        PlayerType.ScaleType.FULL,
                        PlayerType.ScaleType.AUTO);

                List<Integer> listSpeed = Arrays.asList(PlayerType.SpeedType._0_5,
                        PlayerType.SpeedType._1_0,
                        PlayerType.SpeedType._1_5,
                        PlayerType.SpeedType._2_0,
                        PlayerType.SpeedType._2_5,
                        PlayerType.SpeedType._3_0,
                        PlayerType.SpeedType._3_5,
                        PlayerType.SpeedType._4_0,
                        PlayerType.SpeedType._4_5,
                        PlayerType.SpeedType._5_0);

                int length = Math.min(contentData.length, 10);
                for (int i = 0; i < length; i++) {
                    //
                    LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_menu_item_content, viewGroup, true);
                    TextView childAt = (TextView) viewGroup.getChildAt(i);
                    //
                    int value = contentData[i];

                    // 倍速
                    if (listSpeed.contains(value)) {
                        childAt.setTag(new int[]{TYPE_SPEED, value});
                        childAt.setSelected(speedType == value);
                        if (value == PlayerType.SpeedType._0_5) {
                            childAt.setText("0.5");
                        } else if (value == PlayerType.SpeedType._1_5) {
                            childAt.setText("1.5");
                        } else if (value == PlayerType.SpeedType._2_0) {
                            childAt.setText("2.0");
                        } else if (value == PlayerType.SpeedType._2_5) {
                            childAt.setText("2.5");
                        } else if (value == PlayerType.SpeedType._3_0) {
                            childAt.setText("3.0");
                        } else if (value == PlayerType.SpeedType._3_5) {
                            childAt.setText("3.5");
                        } else if (value == PlayerType.SpeedType._4_0) {
                            childAt.setText("4.0");
                        } else if (value == PlayerType.SpeedType._4_5) {
                            childAt.setText("4.5");
                        } else if (value == PlayerType.SpeedType._5_0) {
                            childAt.setText("5.0");
                        } else {
                            childAt.setText("1.0");
                        }
                    }
                    // 画面比例
                    else if (listScale.contains(value)) {
                        childAt.setTag(new int[]{TYPE_SCALE, value});
                        childAt.setSelected(scaleType == value);
                        if (value == PlayerType.ScaleType.REAL) {
                            childAt.setText("原始");
                        } else if (value == PlayerType.ScaleType.FULL) {
                            childAt.setText("全屏");
                        } else if (value == PlayerType.ScaleType._1_1) {
                            childAt.setText("1:1");
                        } else if (value == PlayerType.ScaleType._4_3) {
                            childAt.setText("4:3");
                        } else if (value == PlayerType.ScaleType._5_4) {
                            childAt.setText("5:4");
                        } else if (value == PlayerType.ScaleType._16_9) {
                            childAt.setText("16:9");
                        } else if (value == PlayerType.ScaleType._16_10) {
                            childAt.setText("16:10");
                        } else {
                            childAt.setText("自动");
                        }
                    }
                }
            }

        } catch (Exception e) {
            LogUtil.log("ComponentMenu => showTabGroup => Exception3 " + e.getMessage());
        }
    }


    /**********************/

    public List<MenuInfo> initData() {

        ArrayList<MenuInfo> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {

            // 选集
            if (i == 0) {
                MenuInfo menuInfo = new MenuInfo();
                menuInfo.setName("选集");
                menuInfo.setEpisodeLength(24);
                list.add(menuInfo);
            }
            // 倍速
            else if (i == 1) {
                MenuInfo menuInfo = new MenuInfo();
                menuInfo.setName("倍速");
                menuInfo.setData(new int[]{PlayerType.SpeedType._0_5, PlayerType.SpeedType._1_0, PlayerType.SpeedType._2_0, PlayerType.SpeedType._3_0});
                list.add(menuInfo);
            }
            // 画面比例
            else {
                MenuInfo menuInfo = new MenuInfo();
                menuInfo.setName("画面比例");
                menuInfo.setData(new int[]{PlayerType.ScaleType.DEFAULT, PlayerType.ScaleType.FULL, PlayerType.ScaleType._16_9});
                list.add(menuInfo);
            }
        }

        return list;
    }


//    private void loadEpisodeUrl(@Nullable ImageView imageView, @Nullable String url) {
//        try {
//            imageView.setImageURI(Uri.parse(url));
//        } catch (Exception e) {
//        }
//    }

//    private void loadEpisodeFile(@Nullable ImageView imageView, @Nullable String path) {
//        try {
//            imageView.setImageURI(Uri.parse(path));
//        } catch (Exception e) {
//        }
//    }

//    private void loadEpisodeResource(@Nullable ImageView imageView, @DrawableRes int resId) {
//        try {
//            imageView.setImageResource(resId);
//        } catch (Exception e) {
//        }
//    }

//    public String initEpisodePopuText(int index) {
//        try {
//            return getResources().getString(R.string.module_mediaplayer_string_episode_popu, index + 1);
//        } catch (Exception e) {
//            return null;
//        }
//    }
}
