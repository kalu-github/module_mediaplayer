package lib.kalu.mediaplayer.core.component;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.config.player.PlayerType;
import lib.kalu.mediaplayer.core.player.PlayerApi;
import lib.kalu.mediaplayer.util.MPLogUtil;

@Keep
public class ComponentSeek extends RelativeLayout implements ComponentApi {

    public ComponentSeek(@NonNull Context context) {
        super(context);
        LayoutInflater.from(getContext()).inflate(R.layout.module_mediaplayer_component_seek, this, true);
    }

//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
////        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
////            ProgressBar progressBar = findViewById(R.id.module_mediaplayer_component_seek_pb);
////            progressBar.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
////        }
//
//        try {
//            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
//            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//                @Override
//                public final void onStartTrackingTouch(SeekBar seekBar) {
//                }
//
//                @Override
//                public final void onStopTrackingTouch(SeekBar seekBar) {
//                }
//
//                @Override
//                public final void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
////                    if (fromUser) {
////                        onSeekTo(progress);
////                    } else if (!mTouch) {
////                        onSeekProgressUpdate(progress, 0);
////                    }
//                }
//            });
//        } catch (Exception e) {
//        }
//    }

//    @Override
//    public boolean dispatchKeyEventComponent(KeyEvent event) {
//        // seekForward => start
//        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
//            try {
//                PlayerApi playerApi = getPlayerApi();
//                if (null == playerApi)
//                    throw new Exception("playerApi error: null");
//                if (playerApi.isLive())
//                    throw new Exception("living error: true");
//                if (!playerApi.isPlaying())
//                    throw new Exception("isPlaying error: false");
//                getPlayerApi().callPlayerEvent(PlayerType.StateType.STATE_FAST_FORWARD_START);
//                seekForward(KeyEvent.ACTION_DOWN);
//            } catch (Exception e) {
//                MPLogUtil.log("ComponentSeek => dispatchKeyEventComponent => " + e.getMessage());
//            }
//            return true;
//        }
//        // seekForward => stop
//        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
//            try {
//                PlayerApi playerApi = getPlayerApi();
//                if (null == playerApi)
//                    throw new Exception("playerApi error: null");
//                if (playerApi.isLive())
//                    throw new Exception("living error: true");
//                if (!playerApi.isPlaying())
//                    throw new Exception("isPlaying error: false");
//                getPlayerApi().callPlayerEvent(PlayerType.StateType.STATE_FAST_FORWARD_STOP);
//                seekForward(KeyEvent.ACTION_UP);
//                if (playerApi.isPlaying())
//                    throw new Exception("playing waining: true");
//                playerApi.resume();
//            } catch (Exception e) {
//                MPLogUtil.log("ComponentSeek => dispatchKeyEventComponent => " + e.getMessage());
//            }
//            return true;
//        }
//        // seekRewind => start
//        else if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
//            try {
//                PlayerApi playerApi = getPlayerApi();
//                if (null == playerApi)
//                    throw new Exception("playerApi error: null");
//                if (playerApi.isLive())
//                    throw new Exception("living error: true");
//                if (!playerApi.isPlaying())
//                    throw new Exception("isPlaying error: false");
//                getPlayerApi().callPlayerEvent(PlayerType.StateType.STATE_FAST_REWIND_START);
//                seekRewind(KeyEvent.ACTION_DOWN);
//            } catch (Exception e) {
//                MPLogUtil.log("ComponentSeek => dispatchKeyEventComponent => " + e.getMessage());
//            }
//            return true;
//        }
//        // seekRewind => stop
//        else if (event.getAction() == KeyEvent.ACTION_UP && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
//            try {
//                PlayerApi playerApi = getPlayerApi();
//                if (null == playerApi)
//                    throw new Exception("playerApi error: null");
//                if (playerApi.isLive())
//                    throw new Exception("living error: true");
//                if (!playerApi.isPlaying())
//                    throw new Exception("isPlaying error: false");
//                getPlayerApi().callPlayerEvent(PlayerType.StateType.STATE_FAST_REWIND_STOP);
//                seekForward(KeyEvent.ACTION_UP);
//                if (playerApi.isPlaying())
//                    throw new Exception("playing waining: true");
//                playerApi.resume();
//            } catch (Exception e) {
//                MPLogUtil.log("ComponentSeek => dispatchKeyEventComponent => " + e.getMessage());
//            }
//            return true;
//        }
//        return false;
//    }

    @Override
    public void callPlayerEvent(int playState) {
        switch (playState) {
            case PlayerType.StateType.STATE_FAST_FORWARD_START:
                show();
                break;
            case PlayerType.StateType.STATE_FAST_REWIND_START:
                show();
                break;
            case PlayerType.StateType.STATE_FAST_FORWARD_STOP:
                gone();
                break;
            case PlayerType.StateType.STATE_FAST_REWIND_STOP:
                gone();
                break;
            case PlayerType.StateType.STATE_LOADING_STOP:
            case PlayerType.StateType.STATE_BUFFERING_STOP:
                MPLogUtil.log("ComponentSeek => callPlayerEvent => gone => playState = " + playState);
                gone();
                break;
            case PlayerType.StateType.STATE_INIT:
            case PlayerType.StateType.STATE_ERROR:
            case PlayerType.StateType.STATE_ERROR_IGNORE:
            case PlayerType.StateType.STATE_END:
                onUpdateTimeMillis(0, 0, 0);
                gone();
                break;
        }
    }

    @Override
    public void callWindowEvent(int windowState) {
        switch (windowState) {
            default:
                gone();
                break;
        }
    }

    @Override
    public final void show() {
        try {
            setTag(R.id.module_mediaplayer_component_seek_sb, true);
            bringToFront();
            findViewById(R.id.module_mediaplayer_component_seek_bg).setVisibility(View.VISIBLE);
            findViewById(R.id.module_mediaplayer_component_seek_seekbar).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            MPLogUtil.log("ComponentSeek => show => " + e.getMessage());
        }
    }

    @Override
    public final void gone() {
        try {
            setTag(R.id.module_mediaplayer_component_seek_sb, false);
            findViewById(R.id.module_mediaplayer_component_seek_bg).setVisibility(View.GONE);
            findViewById(R.id.module_mediaplayer_component_seek_seekbar).setVisibility(View.GONE);
        } catch (Exception e) {
            MPLogUtil.log("ComponentSeek => gone => " + e.getMessage());
        }
    }

    @Override
    public final void onUpdateTimeMillis(@NonNull long seek, @NonNull long position, @NonNull long duration) {
        try {
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
            if (null == seekBar)
                throw new Exception("seekbar error: null");
            Object tag = getTag(R.id.module_mediaplayer_component_seek_sb);
            if (null != tag && ((boolean) tag))
                throw new Exception("seekbar warning: user current action down");
            onUpdateSeekProgress(position, duration, true);
        } catch (Exception e) {
            MPLogUtil.log("ComponentSeek => onUpdateTimeMillis => " + e.getMessage());
        }
    }

    @Override
    public final void onUpdateSeekProgress(@NonNull long position, @NonNull long duration, @NonNull boolean updateTime) {
        try {
            SeekBar seekBar = findViewById(R.id.module_mediaplayer_component_seek_sb);
            if (null == seekBar)
                throw new Exception("seekBar error: null");
            seekBar.setProgress((int) position);
            seekBar.setSecondaryProgress((int) position);
            seekBar.setMax((int) duration);
        } catch (Exception e) {
            MPLogUtil.log("ComponentSeek => onUpdateSeekProgress => " + e.getMessage());
        }

        try {
            if (!updateTime)
                throw new Exception();
            long c = position / 1000;
            long c1 = c / 60;
            long c2 = c % 60;
            StringBuilder builderPosition = new StringBuilder();
            if (c1 < 10) {
                builderPosition.append("0");
            }
            builderPosition.append(c1);
            builderPosition.append(":");
            if (c2 < 10) {
                builderPosition.append("0");
            }
            builderPosition.append(c2);
            String strPosition = builderPosition.toString();

            // ms => s
            StringBuilder builderDuration = new StringBuilder();
            long d = duration / 1000;
            long d1 = d / 60;
            long d2 = d % 60;
            if (d1 < 10) {
                builderDuration.append("0");
            }
            builderDuration.append(d1);
            builderDuration.append(":");
            if (d2 < 10) {
                builderDuration.append("0");
            }
            builderDuration.append(d2);
            String strDuration = builderDuration.toString();

            TextView viewMax = findViewById(R.id.module_mediaplayer_component_seek_max);
            viewMax.setText(strDuration);
            TextView viewPosition = findViewById(R.id.module_mediaplayer_component_seek_position);
            viewPosition.setText(strPosition);
        } catch (Exception e) {
            TextView viewMax = findViewById(R.id.module_mediaplayer_component_seek_max);
            viewMax.setText("00:00");
            TextView viewPosition = findViewById(R.id.module_mediaplayer_component_seek_position);
            viewPosition.setText("00:00");
        }
    }

//    @Override
//    public final void onSeekTo(@NonNull int position) {
//        try {
//            PlayerApi playerApi = getPlayerApi();
//            if (null == playerApi)
//                throw new Exception("playerApi error null");
//            playerApi.seekTo(true, position);
//        } catch (Exception e) {
//            MPLogUtil.log("ComponentSeek => onSeekTo => " + e.getMessage());
//        }
//    }
}
