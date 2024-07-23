package com.kalu.mediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.test.TestActivity;
import lib.kalu.mediaplayer.type.PlayerType;
import lib.kalu.mediaplayer.util.UdpUtil;

/**
 * description:
 * created by kalu on 2021/11/23
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAudio();
        initAsset();

        RadioGroup radioGroup = findViewById(R.id.main_kernel);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int visable = (i == R.id.main_kernel_exo_v2 || i == R.id.main_kernel_media3 || i == R.id.main_kernel_ijk ? View.VISIBLE : View.GONE);
                findViewById(R.id.main_decoder_scroll).setVisibility(visable);
                findViewById(R.id.main_decoder_title).setVisibility(visable);
                findViewById(R.id.main_cache).setVisibility(visable);
                findViewById(R.id.main_cache_title).setVisibility(visable);
                findViewById(R.id.main_exo_http).setVisibility(visable);
                findViewById(R.id.main_exo_http_title).setVisibility(visable);
            }
        });


        findViewById(R.id.main_button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1
                initPlayer();
                // 2
                StartArgs args = new StartArgs.Builder()
                        .setUrl(getUrl())
                        .setTitle("测试视频")
                        .setLive(isLive())
                        .setLooping(isLooping())
                        .setPlayWhenReadySeekToPosition(getSeek())
                        .setPlayWhenReadyDelayedTime(getPlayWhenReadyDelayedTime())
                        .setPlayWhenReady(isPlayWhenReady())
                        .setTrySeeDuration(getTrySeeDuration())
                        .setEpisodeItemCount(getEpisodeItemCount())
                        .setEpisodePlayingIndex(4)
                        .setEpisodeFreeItemCount(2)
                        .setEpisodeFlagVipResourceId(R.drawable.ic_vip)
                        .setEpisodeFlagFreeResourceId(R.drawable.ic_free)
                        .build();
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                intent.putExtra(TestActivity.INTENT_ARGS, args);
                startActivity(intent);
            }
        });
    }

    private void initAudio() {
//        PlayerManager.getInstance()
//                .setLog(true)
//                .setAudioKernel(PlayerType.AudioKernelType.AUDIO_IJK_MEDIACODEC)
//                .build();
//        AudioView audioView = findViewById(R.id.player_audio);
//        String s = getResources().getString(R.string.url_tencent);
//        audioView.start(s);
    }

    private boolean isLive() {
        String url = getUrl();
        return "http://39.134.19.248:6610/yinhe/2/ch00000090990000001335/index.m3u8?virtualDomain=yinhe.live_hls.zte.com".equals(url);
    }

    private long getSeek() {
        CheckBox checkBox = findViewById(R.id.main_seek_yes);
        return checkBox.isChecked() ? 10 * 1000L : 0L;
    }

    private int getPlayWhenReadyDelayedTime() {
        CheckBox checkBox = findViewById(R.id.main_play_when_delayed_yes);
        return checkBox.isChecked() ? 3000 : 0;
    }

    private int getEpisodeItemCount() {
        CheckBox checkBox = findViewById(R.id.main_episode_yes);
        return checkBox.isChecked() ? 40 : 0;
    }

    private int getEpisodePlayingIndex() {
        CheckBox checkBox = findViewById(R.id.main_episode_yes);
        return checkBox.isChecked() ? 33 : 0;
    }

    private long getTrySeeDuration() {
        CheckBox checkBox = findViewById(R.id.main_trysee_yes);
        return checkBox.isChecked() ? 20 * 1000L : 0L;
    }

    private boolean isPlayWhenReady() {
        CheckBox checkBox = findViewById(R.id.main_play_when_ready_yes);
        return checkBox.isChecked();
    }

    private boolean isLooping() {
        CheckBox checkBox = findViewById(R.id.main_play_when_looping_yes);
        return checkBox.isChecked();
    }

    private String getUrl() {

        String videoUrl;
        try {
            EditText editText = findViewById(R.id.main_edit);
            String string = editText.getText().toString();
            if (null == string || string.length() == 0)
                throw new Exception();
            videoUrl = string;
        } catch (Exception e) {
            RadioGroup radioGroup = findViewById(R.id.main_radio);
            int id = radioGroup.getCheckedRadioButtonId();
            RadioButton radioButton = radioGroup.findViewById(id);
            videoUrl = radioButton.getTag().toString();
        }

        if ("test_1920~960.mp4".equals(videoUrl)) {
            videoUrl = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + videoUrl;
        } else if ("test_540~960.mp4".equals(videoUrl)) {
            videoUrl = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + videoUrl;
        } else if ("test_544*960.mp4".equals(videoUrl)) {
            videoUrl = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + videoUrl;
        }

        if (videoUrl.startsWith("udp")) {
            boolean checkUdpJoinGroup = UdpUtil.checkUdpJoinGroup(videoUrl);
            Toast.makeText(getApplicationContext(), "checkUdpJoinGroup = " + checkUdpJoinGroup + ", udp = " + videoUrl, Toast.LENGTH_SHORT).show();
        }

        if (!videoUrl.startsWith("http")) {
            File file = new File(videoUrl);
            if (!file.exists()) {
                Toast.makeText(getApplicationContext(), "文件不存在", Toast.LENGTH_SHORT).show();
            }
        }

        return videoUrl;
//        return "http://zteres.sn.chinamobile.com:6060/ystxds/32/movie62ff2023041019270000?AuthInfo=XtytY6od2CoxL3Ece34qrDut5VCPsz5XztCLvxBRpErVaX%2F0PpXSHHk8ZrK18wSwUcPUBpKvvT33aM%2FbcRBNJw%3D%3D&version=v1.0&BreakPoint=0&virtualDomain=ystxds.vod_hpd.zte.com&mescid=00000050280009590769&programid=&contentid=movie62ff2023041019270000&videoid=00000050280009590769&recommendtype=0&userid=A089E4CA0921&boid=&stbid=&terminalflag=1&profilecode=&usersessionid=755219691";
//        return "http://ottrrs.hl.chinamobile.com/88888888/16/20230427/276732502/276732502.ts?rrsip=ottrrs.hl.chinamobile.com&zoneoffset=0&servicetype=0&icpid=&limitflux=-1&limitdur=-1&tenantId=8601&accountinfo=%2C3918822%2C61.185.224.115%2C20230515181603%2C10019232542%2C3918822%2C0.0%2C1%2C0%2C-1%2C4%2C1%2C%2C%2C377747652%2C1%2C%2C377747857%2CEND&GuardEncType=2&it=H4sIAAAAAAAAAE2OQQuCMBzFv82Ow2kWO-xUBEFYoHWNf9tzidPVpkHfPg0PHd_j93u8IZDGYacImckTIIPA6p5L0nWaybVYZWkKWecs4lV4lTJNzjW9LbyZtWu5vYmECyG53HCxFqyaB_eOrEp-bDF2d4QlTGKJ8G40lIk1f1PkZG2ApaHxPT87-lyCWxCGajnXj86xYQ4VxXYq2IPi1ndPCjBHb3-cqslFsCfpliwK6vDnnYKZTnwBm4g0x-0AAAA";
    }

    private void initAsset() {
        List<String> list = Arrays.asList("test_544*960.mp4", "test_1920~960.mp4", "test_540~960.mp4", "test_002.mpeg", "xinzui.mp4", "v_3_4.mp4", "v_1_1.mkv", "video-h265.mkv", "video-test.rmvb", "video-h264-adts.m3u8", "video-h264-adts-0000.ts", "video-h264-adts-0001.ts", "video-sxgd.mpeg");
        for (int i = 0; i < list.size(); i++) {
            String fromPath = list.get(i);
            String savePath = getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fromPath;
            try {
                InputStream is = getApplicationContext().getAssets().open(fromPath);
                FileOutputStream fos = new FileOutputStream(savePath);
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                    // buffer字节
                    fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
                }
                fos.flush();// 刷新缓冲区
                is.close();
                fos.close();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "初始化资源文件 => 错误", Toast.LENGTH_SHORT).show();
                break;
            }
        }
        Toast.makeText(getApplicationContext(), "初始化资源文件 => 成功", Toast.LENGTH_SHORT).show();
    }

    private void initPlayer() {

        @PlayerType.KernelType
        int kernelType;
        int kernelTypeId = ((RadioGroup) findViewById(R.id.main_kernel)).getCheckedRadioButtonId();
        switch (kernelTypeId) {
            case R.id.main_kernel_ijk:
                kernelType = PlayerType.KernelType.IJK;
                break;
            case R.id.main_kernel_exo_v2:
                kernelType = PlayerType.KernelType.EXO_V2;
                break;
            case R.id.main_kernel_vlc:
                kernelType = PlayerType.KernelType.VLC;
                break;
            case R.id.main_kernel_ffplayer:
                kernelType = PlayerType.KernelType.FFPLAYER;
                break;
            case R.id.main_kernel_media3:
                kernelType = PlayerType.KernelType.MEDIA_V3;
                break;
            default:
                kernelType = PlayerType.KernelType.ANDROID;
                break;
        }

        @PlayerType.DecoderType
        int decoderType;
        int decoderId = ((RadioGroup) findViewById(R.id.main_decoder)).getCheckedRadioButtonId();
        switch (decoderId) {
            case R.id.main_exo_decoder_all_ffmpeg:
                decoderType = PlayerType.DecoderType.EXO_ALL_FFMPEG;
                break;
            case R.id.main_exo_decoder_video_codec_audio_ffmpeg:
                decoderType = PlayerType.DecoderType.EXO_VIDEO_CODEC_AUDIO_FFMPEG;
                break;
            case R.id.main_exo_decoder_video_ffmpeg_audio_codec:
                decoderType = PlayerType.DecoderType.EXO_VIDEO_FFMPEG_AUDIO_CODEC;
                break;
            case R.id.main_exo_decoder_only_video_codec:
                decoderType = PlayerType.DecoderType.EXO_ONLY_VIDEO_CODEC;
                break;
            case R.id.main_exo_decoder_only_video_ffmpeg:
                decoderType = PlayerType.DecoderType.EXO_ONLY_VIDEO_FFMPEG;
                break;
            case R.id.main_exo_decoder_only_audio_codec:
                decoderType = PlayerType.DecoderType.EXO_ONLY_AUDIO_CODEC;
                break;
            case R.id.main_exo_decoder_only_audio_ffmpeg:
                decoderType = PlayerType.DecoderType.EXO_ONLY_AUDIO_FFMPEG;
                break;
            case R.id.main_ijk_decoder_all_codec:
                decoderType = PlayerType.DecoderType.IJK_ALL_CODEC;
                break;
            case R.id.main_ijk_decoder_all_ffmpeg:
                decoderType = PlayerType.DecoderType.IJK_ALL_FFMPEG;
                break;
            default:
                decoderType = PlayerType.DecoderType.DEFAULT;
                break;
        }

        @PlayerType.RenderType
        int renderType;
        int renderTypeId = ((RadioGroup) findViewById(R.id.main_render)).getCheckedRadioButtonId();
        switch (renderTypeId) {
            case R.id.main_render_textureview:
                renderType = PlayerType.RenderType.TEXTURE_VIEW;
                break;
            case R.id.main_render_glsurfaceview:
                renderType = PlayerType.RenderType.GL_SURFACE_VIEW;
                break;
            default:
                renderType = PlayerType.RenderType.SURFACE_VIEW;
                break;
        }

        @PlayerType.ScaleType
        int scaleType;
        int scaleTypeId = ((RadioGroup) findViewById(R.id.main_scale)).getCheckedRadioButtonId();
        switch (scaleTypeId) {
            case R.id.main_scale2:
                scaleType = PlayerType.ScaleType.REAL;
                break;
            case R.id.main_scale3:
                scaleType = PlayerType.ScaleType.FULL;
                break;
            case R.id.main_scale4:
                scaleType = PlayerType.ScaleType._1_1;
                break;
            case R.id.main_scale5:
                scaleType = PlayerType.ScaleType._4_3;
                break;
            case R.id.main_scale6:
                scaleType = PlayerType.ScaleType._5_4;
                break;
            case R.id.main_scale7:
                scaleType = PlayerType.ScaleType._16_9;
                break;
            case R.id.main_scale8:
                scaleType = PlayerType.ScaleType._16_10;
                break;
            default:
                scaleType = PlayerType.ScaleType.DEFAULT;
                break;
        }

        @PlayerType.NetType
        int netType;
        int httpTypeId = ((RadioGroup) findViewById(R.id.main_exo_http)).getCheckedRadioButtonId();
        switch (httpTypeId) {
            case R.id.main_exo_http_okhttp:
                netType = PlayerType.NetType.EXO_OKHTTP;
                break;
            default:
                netType = PlayerType.NetType.DEFAULT;
                break;
        }

        @PlayerType.CacheType
        int cacheType;
        int cacheFlagId = ((RadioGroup) findViewById(R.id.main_cache)).getCheckedRadioButtonId();
        switch (cacheFlagId) {
            case R.id.main_cache_yes:
                cacheType = PlayerType.CacheType.EXO_OPEN;
                break;
            default:
                cacheType = PlayerType.CacheType.DEFAULT;
                break;
        }

        PlayerSDK.init()
                // 日志开关
                .setLog(true)
                // 数据埋点（监听播放器操作日志）
                .setBuriedEvent(new LogBuriedEvent())
                // 播放器类型（MediaPlayer Media3Player ExoPlayer IjkPLayer）
                .setKernelType(kernelType)
                // 渲染类型（TextuteView SurafecView）
                .setRenderType(renderType)
                // 解码器类型（仅针对 Media3Player ExoPlayer IjkPLayer）
                .setDecoderType(decoderType)
                // 画面比例（自动 全屏 原始 1:1 4:3 5:4 16:9 16:10）
                .setScaleType(scaleType)
                // 超时时间（默认20s）
                .setConnectTimeout(20000)
                // 缓冲超时重播（默认false）
                .setBufferingTimeoutRetry(false)
                // 播放器每次播放器都销毁（默认false）
                .setInitRelease(false)
                // 播放器生命周期自动销毁（默认true）
                .setSupportAutoRelease(false)
                // 缓存类型（仅针对 ExoPlayer）
                .setCacheType(cacheType)
                // 缓存类型（仅针对 ExoPlayer）
                .setCacheLocalType(PlayerType.CacheLocalType.DEFAULT)
                // 缓存大小（仅针对 ExoPlayer）
                .setCacheSizeType(PlayerType.CacheSizeType.DEFAULT)
                // 缓存文件夹（仅针对 ExoPlayer）
                .setCacheDirName(null)
                // 快进类型（仅针对 MediaPlayer ExoPlayer IjkPlayer）
                .setSeekType(PlayerType.SeekType.DEFAULT)
                // 网络类型（仅针对 ExoPlayer）
                .setNetType(netType)
                .build();
    }
}