package lib.kalu.mediaplayer.test;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;

import org.json.JSONArray;

import java.util.LinkedList;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.core.component.ComponentBuffering;
import lib.kalu.mediaplayer.core.component.ComponentComplete;
import lib.kalu.mediaplayer.core.component.ComponentError;
import lib.kalu.mediaplayer.core.component.ComponentInit;
import lib.kalu.mediaplayer.core.component.ComponentLoadingGradient;
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
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerLayout;

/**
 * @description: 横屏全屏视频播放器
 * @date: 2021-05-25 10:37
 */
public final class TestActivity extends Activity {


    public static final int RESULT_CODE = 31001;

    public static final String INTENT_PLAY_WHEN_READY = "intent_play_when_ready";
    public static final String INTENT_LIVE = "intent_live"; // live

    public static final String INTENT_TRY_SEE = "intent_try_see"; // 试看

    public static final String INTENT_SEEK = "intent_seek"; // 快进

    public static final String INTENT_DATA = "intent_data"; // 外部传入DATA

    public static final String INTENT_EPISODE = "intent_episode"; // 选集
    public static final String INTENT_EPISODE_PLAY_INDEX = "intent_episode_play_index"; // 选集
    public static final String INTENT_EPISODE_ITEM_COUNT = "intent_episode_item_count"; // 选集
    public static final String INTENT_URL = "intent_url"; // 视频Url

    public static final String INTENT_SRT = "intent_srt"; // 字幕Url

    public static final String INTENT_TIME_BROWSING = "intent_time_browsing"; // 视频浏览时长

    public static final String INTENT_TIME_LENGTH = "intent_time_length"; // 视频总时长

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
        LinkedList<ComponentApi> componentApis = new LinkedList<>();
        // menu
        ComponentMenu menu = new ComponentMenu(getApplicationContext());
        componentApis.add(menu);
        // loading
        ComponentLoadingGradient loading = new ComponentLoadingGradient(getApplicationContext());
        loading.setComponentShowNetSpeed(true);
        loading.setComponentBackgroundColorInt(Color.BLACK);
        componentApis.add(loading);
        // seek
        ComponentSeek seek = new ComponentSeek(getApplicationContext());
        seek.initSeekBarChangeListener();
        componentApis.add(seek);
        // complete
        ComponentComplete end = new ComponentComplete(getApplicationContext());
        componentApis.add(end);
        // error
        ComponentError error = new ComponentError(getApplicationContext());
        error.setComponentBackgroundColorInt(Color.BLACK);
        componentApis.add(error);
        // net
        ComponentBuffering speed = new ComponentBuffering(getApplicationContext());
        componentApis.add(speed);
        // init
        ComponentInit init = new ComponentInit(getApplicationContext());
        componentApis.add(init);
        // pause
        ComponentPause pause = new ComponentPause(getApplicationContext());
        componentApis.add(pause);
        // try
        ComponentWarningTrySee trys = new ComponentWarningTrySee(getApplicationContext());
        componentApis.add(trys);
        // 起播详情
        ComponentWarningPlayInfo info = new ComponentWarningPlayInfo(getApplicationContext());
        componentApis.add(info);
        // insert-component
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        playerLayout.addAllComponent(componentApis);
    }

    private void initListener() {
        // playerLayout
        PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
        playerLayout.setOnPlayerEpisodeListener(new OnPlayerEpisodeListener() {
            @Override
            public void onEpisode(int pos) {
                getIntent().putExtra(INTENT_EPISODE_PLAY_INDEX, pos);
                startPlayer();
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

    private String getUrl() {
        return getIntent().getStringExtra(INTENT_URL);
    }

    private void startPlayer() {

        String url = getUrl();
        if (null == url) {
            onBackPressed();
            return;
        }

        StartArgs.Builder builder = new StartArgs.Builder();
        builder.setUrl(url);

        // 试看 10s
        boolean trySee = getIntent().getBooleanExtra(INTENT_TRY_SEE, false);
        builder.setTrySeeDuration(trySee ? 10 * 1000L : 0L);

        // 续播 10s
        boolean isSeek = getIntent().getBooleanExtra(INTENT_SEEK, false);
        if (isSeek) {
            builder.setSeek(33000L);
        } else {
            builder.setSeek(0L);
        }

        boolean episode = getIntent().getBooleanExtra(INTENT_EPISODE, false);
        if (episode) {
            int playIndex = getIntent().getIntExtra(INTENT_EPISODE_PLAY_INDEX, -1);
            int itemCount = getIntent().getIntExtra(INTENT_EPISODE_ITEM_COUNT, -1);
            builder.setEpisodePlayingIndex(playIndex);
            builder.setEpisodeItemCount(itemCount);
        } else {
            builder.setEpisodePlayingIndex(-1);
            builder.setEpisodeItemCount(-1);
        }

        if (episode) {
            int playIndex = getIntent().getIntExtra(INTENT_EPISODE_PLAY_INDEX, -1);
            builder.setTitle("测试视频 第" + (playIndex + 1) + "集");
        } else {
            builder.setTitle("测试视频");
        }

        boolean live = getIntent().getBooleanExtra(INTENT_LIVE, false);
        builder.setLive(live);

        boolean playWhenReady = getIntent().getBooleanExtra(INTENT_PLAY_WHEN_READY, false);
        builder.setPlayWhenReady(playWhenReady);

        StartArgs build = builder.build();
        PlayerLayout videoLayout = findViewById(R.id.module_mediaplayer_test_video);
        videoLayout.start(build);
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
    public void finish() {

        PlayerLayout videoLayout = findViewById(R.id.module_mediaplayer_test_video);
        long browsing = videoLayout.getPosition() / 1000;
        if (browsing < 0) {
            browsing = 0;
        }
        long duration = videoLayout.getDuration() / 1000;
        if (duration < 0) {
            duration = 0;
        }
        String extra = getIntent().getStringExtra(INTENT_DATA);
        Intent intent = new Intent();
        intent.putExtra(INTENT_DATA, extra);
        intent.putExtra(INTENT_TIME_LENGTH, duration);
        intent.putExtra(INTENT_TIME_BROWSING, browsing);
        setResult(RESULT_CODE, intent);
        super.finish();
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
        videoLayout.stop(false);
        videoLayout.release(true);
    }
}