package lib.kalu.mediaplayer.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import org.json.JSONArray;

import java.util.LinkedList;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.core.component.ComponentLoadingGradient;
import lib.kalu.mediaplayer.core.component.ComponentWarningPlayInfo;
import lib.kalu.mediaplayer.core.component.ComponentMenu;
import lib.kalu.mediaplayer.core.component.ComponentWarningPlayRecord;
import lib.kalu.mediaplayer.listener.OnPlayerItemsLiatener;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.core.component.ComponentApi;
import lib.kalu.mediaplayer.core.component.ComponentComplete;
import lib.kalu.mediaplayer.core.component.ComponentError;
import lib.kalu.mediaplayer.core.component.ComponentInit;
import lib.kalu.mediaplayer.core.component.ComponentLoading;
import lib.kalu.mediaplayer.core.component.ComponentBuffering;
import lib.kalu.mediaplayer.core.component.ComponentPause;
import lib.kalu.mediaplayer.core.component.ComponentSeek;
import lib.kalu.mediaplayer.core.component.ComponentTrySee;
import lib.kalu.mediaplayer.listener.OnPlayerEventListener;
import lib.kalu.mediaplayer.listener.OnPlayerProgressListener;
import lib.kalu.mediaplayer.listener.OnPlayerWindowListener;
import lib.kalu.mediaplayer.util.LogUtil;
import lib.kalu.mediaplayer.widget.player.PlayerLayout;

/**
 * @description: 横屏全屏视频播放器
 * @date: 2021-05-25 10:37
 */
public final class TestActivity extends Activity {


    public static final int RESULT_CODE = 31001;

    public static final String INTENT_LIVE = "intent_live"; // live

    public static final String INTENT_TRY_SEE = "intent_try_see"; // 试看

    public static final String INTENT_SEEK = "intent_seek"; // 快进

    public static final String INTENT_DATA = "intent_data"; // 外部传入DATA

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

        // 音轨信息
        findViewById(R.id.module_mediaplayer_test_button21).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
                JSONArray trackInfo = playerLayout.getTrackInfo();
                LogUtil.log("trackInfo = " + trackInfo);
            }
        });
        // 音轨信息2
        findViewById(R.id.module_mediaplayer_test_button22).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
                playerLayout.switchTrack(1);
            }
        });
        // 音轨信息3
        findViewById(R.id.module_mediaplayer_test_button23).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
                playerLayout.switchTrack(2);
            }
        });
        // 信息
        findViewById(R.id.module_mediaplayer_test_button8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
//                VideoIjkPlayer videoIjkPlayer = playerLayout.getKernel();
//                IjkTrackInfo[] trackInfo = videoIjkPlayer.getTrackInfo();
//                String s = new Gson().toJson(trackInfo);
//                MPLogUtil.log("TestActivity => onClick => "+s);
            }
        });
        // 跳转
        findViewById(R.id.module_mediaplayer_test_button0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                intent.putExtra(TestActivity.INTENT_URL, getUrl());
                startActivity(intent);
            }
        });
        // 换台
        findViewById(R.id.module_mediaplayer_test_button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlayer();
            }
        });
        // 全屏
        findViewById(R.id.module_mediaplayer_test_button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFull();
            }
        });
        // 浮动
        findViewById(R.id.module_mediaplayer_test_button3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFloat();
            }
        });
        findViewById(R.id.module_mediaplayer_test_button4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
                playerLayout.showComponent(ComponentSeek.class);
            }
        });
        findViewById(R.id.module_mediaplayer_test_button5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerLayout playerLayout = findViewById(R.id.module_mediaplayer_test_video);
                playerLayout.hideComponent(ComponentSeek.class);
            }
        });
    }

    private void initComponent() {
        LinkedList<ComponentApi> componentApis = new LinkedList<>();
        // menu
        ComponentMenu menu = new ComponentMenu(getApplicationContext());
        menu.setItemsData(64, 66);
        componentApis.add(menu);
        // loading
        ComponentLoadingGradient loading = new ComponentLoadingGradient(getApplicationContext());
        loading.setComponentText("正在播放 第" + (64 + 1) + "集");
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
        ComponentTrySee trys = new ComponentTrySee(getApplicationContext());
        componentApis.add(trys);
        // 续播
        ComponentWarningPlayRecord playRecord = new ComponentWarningPlayRecord(getApplicationContext());
        componentApis.add(playRecord);
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
        playerLayout.setOnPlayerItemsListener(new OnPlayerItemsLiatener() {
            @Override
            public void onItem(int pos) {
                updateTitle(pos);
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
            public void onRestart() {

            }

            @Override
            public void onStart() {

            }

            @Override
            public void onPause() {

            }

            @Override
            public void onResume() {

            }

            @Override
            public void onEvent(int state) {
                LogUtil.log("onPlayStateChanged => state = " + state);

                switch (state) {
                    case PlayerType.StateType.STATE_INIT:
                        //播放未开始，初始化
                        break;
                    case PlayerType.StateType.STATE_START_ABORT:
                        //开始播放中止
                        break;
                    case PlayerType.StateType.STATE_LOADING_START:
                        //播放准备中
                        break;
                    case PlayerType.StateType.STATE_LOADING_STOP:
                        //播放准备就绪
                        break;
                    case PlayerType.StateType.STATE_ERROR:
                        //播放错误
                        break;
                    case PlayerType.StateType.STATE_BUFFERING_START:
                        //正在缓冲
                        break;
                    case PlayerType.StateType.STATE_START:
                        //正在播放
                        break;
                    case PlayerType.StateType.STATE_PAUSE:
                        //暂停播放
                        break;
                    case PlayerType.StateType.STATE_BUFFERING_STOP:
                        //暂停缓冲
                        break;
                    case PlayerType.StateType.STATE_END:
                        //播放完成
                        break;
                }
            }
        });
    }

    private void updateTitle(int pos) {
        Toast.makeText(getApplicationContext(), "切换选集 " + (pos + 1), Toast.LENGTH_SHORT).show();
        PlayerLayout videoLayout = findViewById(R.id.module_mediaplayer_test_video);
        ComponentLoading loading = videoLayout.findComponent(ComponentLoading.class);
        loading.setComponentText("正在播放 第" + (pos + 1) + "集");
    }

    private String getUrl() {
        return getIntent().getStringExtra(INTENT_URL);
    }

    private void startPlayer() {

        String url = getUrl();
        if (null == url || url.length() == 0) {
            onBackPressed();
            return;
        }

        boolean live = getIntent().getBooleanExtra(INTENT_LIVE, false);

        StartArgs.Builder builder = new StartArgs.Builder();

        // 试看 45s
        boolean trySee = getIntent().getBooleanExtra(INTENT_TRY_SEE, false);
        if (trySee) {
            builder.setTrySee(true);
            builder.setMaxDuration(45 * 1000);
        } else {
            builder.setTrySee(false);
        }

        // 续播 10s
        boolean isSeek = getIntent().getBooleanExtra(INTENT_SEEK, false);
        if (isSeek) {
            builder.setSeek(33000L);
        } else {
            builder.setSeek(0L);
        }

        builder.setLive(live);
        builder.setTitle("测试title");
        builder.setUrl(url);
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

//    @Override
//    protected void onResume() {
//        super.onResume();
//        PlayerLayout videoLayout = findViewById(R.id.module_mediaplayer_test_video);
//        videoLayout.resume(false);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        PlayerLayout videoLayout = findViewById(R.id.module_mediaplayer_test_video);
//        videoLayout.pause(false);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        PlayerLayout videoLayout = findViewById(R.id.module_mediaplayer_test_video);
//        videoLayout.release(true);
//    }
}