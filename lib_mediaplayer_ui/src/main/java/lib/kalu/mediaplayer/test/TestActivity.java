package lib.kalu.mediaplayer.test;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

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
import lib.kalu.mediaplayer.core.component.ComponentWarningPlayInfo;
import lib.kalu.mediaplayer.core.component.ComponentWarningTrySee;
import lib.kalu.mediaplayer.listener.OnPlayerEpisodeListener;
import lib.kalu.mediaplayer.listener.OnPlayerEventListener;
import lib.kalu.mediaplayer.listener.OnPlayerProgressListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowListener;
import lib.kalu.mediaplayer.type.PlayerType;
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
        setContentView(R.layout.module_mediaplayer_test);

        initListener();
        initComponent();
        startPlayer();

//        // 音轨信息
//        findViewById(R.id.module_mediaplayer_test_button21).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
//                JSONArray trackInfo = playerLayout.getTrackInfo();
//                LogUtil.log("trackInfo = " + trackInfo);
//            }
//        });
//        // 音轨信息2
//        findViewById(R.id.module_mediaplayer_test_button22).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
//                playerLayout.switchTrack(1);
//            }
//        });
//        // 音轨信息3
//        findViewById(R.id.module_mediaplayer_test_button23).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
//                playerLayout.switchTrack(2);
//            }
//        });
//        // 信息
//        findViewById(R.id.module_mediaplayer_test_button8).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
////                VideoIjkPlayer videoIjkPlayer = playerLayout.getKernel();
////                IjkTrackInfo[] trackInfo = videoIjkPlayer.getTrackInfo();
////                String s = new Gson().toJson(trackInfo);
////                MPLogUtil.log("TestActivity => onClick => "+s);
//            }
//        });
//        // 跳转
//        findViewById(R.id.module_mediaplayer_test_button0).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
//                intent.putExtra(TestActivity.INTENT_URL, getUrl());
//                startActivity(intent);
//            }
//        });
//        // 换台
//        findViewById(R.id.module_mediaplayer_test_button1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startPlayer();
//            }
//        });
//        // 全屏
//        findViewById(R.id.module_mediaplayer_test_button2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startFull();
//            }
//        });
//        // 浮动
//        findViewById(R.id.module_mediaplayer_test_button3).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startFloat();
//            }
//        });
    }

    private void initComponent() {
        try {
            PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
            // menu
            ComponentMenu menu = new ComponentMenu(getApplicationContext());
            playerLayout.addComponent(menu);
            // loading
            ComponentPrepareGradient loading = new ComponentPrepareGradient(getApplicationContext());
            loading.setComponentShowNetSpeed(true);
            loading.setComponentBackgroundColorInt(Color.BLACK);
            playerLayout.addComponent(loading);
            // seek
            ComponentSeek seek = new ComponentSeek(getApplicationContext());
            seek.initSeekBarChangeListener();
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
        } catch (Exception e) {
        }
    }

    private void initListener() {
        // playerLayout
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        playerLayout.setOnPlayerEpisodeListener(new OnPlayerEpisodeListener() {
            @Override
            public void onEpisode(int pos) {
                startPlayer(pos);
            }

            @Override
            public void onEnd() {
                startPlayer(0);
            }
        });
        playerLayout.setOnPlayerWindowListener(new OnPlayerWindowListener() {
            @Override
            public void onWindow(int state) {
                switch (state) {
                    case PlayerType.WindowType.NORMAL:
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
            videoLayout.start(args);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void startPlayer(int position) {

        try {
            StartArgs args = (StartArgs) getIntent().getSerializableExtra(INTENT_ARGS);
            if (null == args)
                throw new Exception("error: args null");
            StartArgs newArgs = args.newBuilder().setEpisodePlayingIndex(position).build();
            PlayerLayout videoLayout = findViewById(R.id.module_mediaplayer_test_video);
            videoLayout.start(newArgs);
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