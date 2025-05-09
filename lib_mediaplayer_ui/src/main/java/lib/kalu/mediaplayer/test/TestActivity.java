package lib.kalu.mediaplayer.test;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import org.json.JSONArray;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.core.component.ComponentBuffering;
import lib.kalu.mediaplayer.core.component.ComponentComplete;
import lib.kalu.mediaplayer.core.component.ComponentError;
import lib.kalu.mediaplayer.core.component.ComponentInit;
import lib.kalu.mediaplayer.core.component.ComponentPrepareGradient;
import lib.kalu.mediaplayer.core.component.ComponentMenu;
import lib.kalu.mediaplayer.core.component.ComponentPause;
import lib.kalu.mediaplayer.core.component.ComponentSeek;
import lib.kalu.mediaplayer.core.component.ComponentSubtitle;
import lib.kalu.mediaplayer.core.component.ComponentWarningPlayInfo;
import lib.kalu.mediaplayer.core.component.ComponentWarningTrySee;
import lib.kalu.mediaplayer.listener.OnPlayerEventListener;
import lib.kalu.mediaplayer.listener.OnPlayerProgressListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowListener;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerLayout;

/**
 * @description: 横屏全屏视频播放器
 * @date: 2021-05-25 10:37
 */
public final class TestActivity extends Activity {

    public static final int RESULT_CODE = 31001;
    public static final String INTENT_ARGS = "intent_args";

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

//        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
//        boolean dispatchKeyEvent = playerLayout.dispatchKeyEvent(event);
//        if (dispatchKeyEvent) {
//            return true;
//        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        setContentView(R.layout.module_mediaplayer_test_activity);

        initListener();
        initComponent();
        startPlayer();

        // 视频轨道
        findViewById(R.id.module_mediaplayer_track_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTrackInfo(1);
            }
        });
        // 音频轨道
        findViewById(R.id.module_mediaplayer_track_audio).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTrackInfo(2);
            }
        });
        // 字幕轨道
        findViewById(R.id.module_mediaplayer_track_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTrackInfo(3);
            }
        });
    }

    public void setTrackVideo() {
    }

    public void setTrackAudio() {
    }

    public void setTrackSubtitle(String language) {
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        playerLayout.setTrackSubtitle(language);
    }

    private void showTrackInfo(int type) {
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        JSONArray trackInfo;
        if (type == 1) {
            trackInfo = playerLayout.getTrackInfoVideo();
        } else if (type == 2) {
            trackInfo = playerLayout.getTrackInfoAudio();
        } else {
            trackInfo = playerLayout.getTrackInfoSubtitle();
        }
        LogUtil.log("showTrackInfo -> type = " + type + ", trackInfo = " + trackInfo);

        if (null != trackInfo) {
            Bundle bundle = new Bundle();
            bundle.putInt(TestDialog.BUNDLE_TYPE, type);
            bundle.putString(TestDialog.BUNDLE_DATA, trackInfo.toString());

            TestDialog testDialog = new TestDialog();
            testDialog.setArguments(bundle);
            testDialog.show(getFragmentManager(), "TestDialog");
        }
    }

    private void initComponent() {
        try {
            PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
            // menu
            ComponentMenu menu = new ComponentMenu(getApplicationContext());
            playerLayout.addComponent(menu);
            // loading
            ComponentPrepareGradient loading = new ComponentPrepareGradient(getApplicationContext());
            loading.setComponentBackgroundColorInt(Color.BLACK);
            playerLayout.addComponent(loading);
            // seek
            ComponentSeek seek = new ComponentSeek(getApplicationContext());
            playerLayout.addComponent(seek);
            // complete
            ComponentComplete end = new ComponentComplete(getApplicationContext());
            playerLayout.addComponent(end);
            // error
            ComponentError error = new ComponentError(getApplicationContext());
            error.setComponentBackgroundColorInt(Color.BLACK);
            playerLayout.addComponent(error);
            // net
            ComponentBuffering speed = new ComponentBuffering(getApplicationContext());
            playerLayout.addComponent(speed);
            // init
            ComponentInit init = new ComponentInit(getApplicationContext());
            playerLayout.addComponent(init);
            // pause
            ComponentPause pause = new ComponentPause(getApplicationContext());
            playerLayout.addComponent(pause);
            // try
            ComponentWarningTrySee trys = new ComponentWarningTrySee(getApplicationContext());
            playerLayout.addComponent(trys);
            // 起播详情
            ComponentWarningPlayInfo info = new ComponentWarningPlayInfo(getApplicationContext());
            playerLayout.addComponent(info);
            // 字幕
            ComponentSubtitle subtitle = new ComponentSubtitle(getApplicationContext());
            playerLayout.addComponent(subtitle);
        } catch (Exception e) {
        }
    }

    private void initListener() {
        // playerLayout
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        playerLayout.setOnPlayerWindowListener(new OnPlayerWindowListener() {
            @Override
            public void onWindow(int state) {
                switch (state) {
                    case PlayerType.WindowType.DEFAULT:
                        //普通模式
                        break;
                    case PlayerType.WindowType.FULL:
                        //全屏模式
                        break;
                    case PlayerType.WindowType.FLOAT:
                        //小屏模式
                        break;
                }
            }
        });
        playerLayout.setOnPlayerProgressListener(new OnPlayerProgressListener() {
            @Override
            public void onProgress(long position, long duration) {
            }
        });
        playerLayout.setOnPlayerEventListener(new OnPlayerEventListener() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onError(String info) {

            }
        });
    }

    private void startPlayer() {

        try {
            StartArgs args = (StartArgs) getIntent().getSerializableExtra(INTENT_ARGS);
            if (null == args)
                throw new Exception("error: args null");
            PlayerLayout videoLayout = findViewById(R.id.module_mediaplayer_test_video);
            videoLayout.setOnPlayerEventListener(new OnPlayerEventListener() {
                @Override
                public void onComplete() {

                }

                @Override
                public void onStart() {
                    PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
                    playerLayout.getTrackInfoAll();
                }

                @Override
                public void onError(String info) {

                }
            });
            videoLayout.start(args);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void startFull() {
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        playerLayout.startFull();
    }

    private void stopFull() {
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        playerLayout.stopFull();
    }

    private void startFloat() {
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        playerLayout.startFloat();
    }

    private void stopFloat() {
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        playerLayout.stopFloat();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PlayerLayout videoLayout = findViewById(R.id.module_mediaplayer_test_video);
        videoLayout.resume(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PlayerLayout videoLayout = findViewById(R.id.module_mediaplayer_test_video);
        videoLayout.pause(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PlayerLayout videoLayout = findViewById(R.id.module_mediaplayer_test_video);
        videoLayout.stop();
        videoLayout.release();
    }
}