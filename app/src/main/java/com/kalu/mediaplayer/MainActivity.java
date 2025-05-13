package com.kalu.mediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lib.kalu.mediaplayer.PlayerSDK;
import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.args.TrackArgs;
import lib.kalu.mediaplayer.test.TestActivity;
import lib.kalu.mediaplayer.type.PlayerType;

/**
 * description:
 * created by kalu on 2021/11/23
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        findViewById(R.id.main_button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1
                initPlayer();
                // 2
                StartArgs args = new StartArgs.Builder()
                        .setUrl(getUrl())
                        .setExtraTrackSubtitle(getExtraSubtitle())
                        .setExtraTrackVideo(getExtraVideo())
                        .setTitle("测试视频")
                        .setLive(isLive())
                        .setLooping(isLooping())
                        .setPlayWhenReadySeekToPosition(getSeek())
                        .setPlayWhenReadyDelayedTime(getPlayWhenReadyDelayedTime())
                        .setPlayWhenReady(isPlayWhenReady())
                        .setTrySeeDuration(getTrySeeDuration())
                        .build();
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                intent.putExtra(TestActivity.INTENT_ARGS, args);
                startActivity(intent);
            }
        });
    }

    private void init() {

        // decoders
        try {
            String[] strings = getResources().getStringArray(R.array.decoders);
            ViewGroup viewGroup = findViewById(R.id.main_decoder);
            for (int i = 0; i < strings.length; i++) {
                LayoutInflater.from(this).inflate(R.layout.activity_main_radio_button, viewGroup);
                RadioButton radioButton = (RadioButton) viewGroup.getChildAt(i);
                radioButton.setText(strings[i]);
            }
        } catch (Exception e) {
        }


        // kernel
        try {
            String[] kernels = getResources().getStringArray(R.array.kernels);
            ViewGroup viewGroup = findViewById(R.id.main_kernel_group);
            for (int i = 0; i < kernels.length; i++) {
                LayoutInflater.from(this).inflate(R.layout.activity_main_radio_button, viewGroup);
                RadioButton radioButton = (RadioButton) viewGroup.getChildAt(i);
                radioButton.setText(kernels[i]);
            }
        } catch (Exception e) {
        }

        // url
        try {
            String[] names = getResources().getStringArray(R.array.names);
            String[] urls = getResources().getStringArray(R.array.urls);
            if (names.length != urls.length)
                throw new Exception();
            ViewGroup viewGroup = findViewById(R.id.main_urls);
            for (int i = 0; i < names.length; i++) {
                LayoutInflater.from(this).inflate(R.layout.activity_main_radio_button, viewGroup);
                RadioButton radioButton = (RadioButton) viewGroup.getChildAt(i);
                radioButton.setText(names[i]);
                radioButton.setTag(urls[i]);
            }
        } catch (Exception e) {
        }

        // copy
        try {
            List<String> list = Arrays.asList("test.mp4", "test.vtt");
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
        } catch (Exception e) {
        }
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


    private List<TrackArgs> getExtraSubtitle() {
        /**
         *  2025-04-25 20:01:32.609 25895-25932 PlayerViewModel         com.yyt.zapptv                       D  streamsList = [# com.yyt.zapptv.model.proto.Playback$StreamTrack@37d8a524
         *     format: "hls"
         *     label: "HD"
         *     quality: "HD"
         *     url: "https://media-1347269025.cos.sa-saopaulo.myqcloud.com/movie/5f2751d1/720p/index.m3u8", # com.yyt.zapptv.model.proto.Playback$StreamTrack@35014555
         *     format: "hls"
         *     label: "SD"
         *     quality: "SD"
         *     url: "https://media-1347269025.cos.sa-saopaulo.myqcloud.com/movie/5f2751d1/480p/index.m3u8", # com.yyt.zapptv.model.proto.Playback$StreamTrack@5f9f9c57
         *     format: "hls"
         *     is_login: true
         *     label: "FULL HD"
         *     quality: "FHD"
         *     url: "https://media-1347269025.cos.sa-saopaulo.myqcloud.com/movie/5f2751d1/1080p/index.m3u8"]
         *     2025-04-25 20:01:32.612 25895-25932 PlayerViewModel         com.yyt.zapptv                       D  subtitlesList = [# com.yyt.zapptv.model.proto.Playback$SubtitleTrack@b7ab61ea
         *     format: "vtt"
         *     label: "English"
         *     language: "en"
         *     url: "https://media-1347269025.cos.sa-saopaulo.myqcloud.com/movie/5f2751d1/subtitles/English.vtt", # com.yyt.zapptv.model.proto.Playback$SubtitleTrack@6d66d2c6
         *     format: "vtt"
         *     label: "Espa\303\261ol"
         *     language: "es"
         *     url: "https://media-1347269025.cos.sa-saopaulo.myqcloud.com/movie/5f2751d1/subtitles/Spanish.vtt", # com.yyt.zapptv.model.proto.Playback$SubtitleTrack@925f4fa3
         *     format: "vtt"
         *     label: "Portugu\303\252s (Brasil)"
         *     language: "pt"
         *     url: "https://media-1347269025.cos.sa-saopaulo.myqcloud.com/movie/5f2751d1/subtitles/Portuguese.vtt"]
         *     2025-04-25 20:01:32.612 25895-25932 PlayerViewModel         com.yyt.zapptv                       D  audiosList = []
         */


        try {
            RadioGroup radioGroup = findViewById(R.id.main_urls);
            int childCount = radioGroup.getChildCount();
            for (int n = 0; n < childCount; n++) {
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(n);
                boolean checked = radioButton.isChecked();
                if (checked && n == 3) {
                    ArrayList<TrackArgs> list = new ArrayList<>();
                    String[] urls = getResources().getStringArray(R.array.hls_extra_subtitle_urls);
                    String[] languages = getResources().getStringArray(R.array.hls_extra_subtitle_languages);
                    for (int i = 0; i < 3; i++) {
                        TrackArgs subtitleTrack = new TrackArgs();
                        subtitleTrack.setRoleFlags((int) System.nanoTime());
                        subtitleTrack.setLanguage(languages[i]);
                        subtitleTrack.setUrl(urls[i]);
                        subtitleTrack.setMimeType(PlayerType.TrackType.TEXT_VTT);
                        //
                        list.add(subtitleTrack);
                    }

                    return list;
                }
            }
            throw new Exception();
        } catch (Exception e) {
            return null;
        }
    }

    private List<TrackArgs> getExtraVideo() {

        try {
            RadioGroup radioGroup = findViewById(R.id.main_urls);
            int childCount = radioGroup.getChildCount();
            for (int n = 0; n < childCount; n++) {
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(n);
                boolean checked = radioButton.isChecked();
                if (checked && n == 3) {
                    ArrayList<TrackArgs> list = new ArrayList<>();
                    String[] urls = getResources().getStringArray(R.array.hls_extra_video_urls);
                    for (int i = 0; i < 3; i++) {
                        TrackArgs trackArgs = new TrackArgs();
                        trackArgs.setRoleFlags((int) System.nanoTime());
                        trackArgs.setUrl(urls[i]);
//                        subtitleTrack.setMimeType(PlayerType.TrackType.TEXT_VTT);
                        //
                        list.add(trackArgs);
                    }

                    return list;
                }
            }
            throw new Exception();
        } catch (Exception e) {
            return null;
        }
    }

    private String getUrl() {

//        return getApplicationContext().getFilesDir().getAbsolutePath() + "/test.mp4";

        try {

            //
            RadioGroup radioGroup = findViewById(R.id.main_urls);
            int childCount = radioGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
                boolean checked = radioButton.isChecked();
                if (checked) {
                    return radioButton.getTag().toString();
                }
            }

            //
            EditText editText = findViewById(R.id.main_edit);
            Editable editableText = editText.getEditableText();
            if (editableText.length() > 0)
                return editableText.toString();

            //
            throw new Exception();
        } catch (Exception e) {
            return "";
        }
    }

    private int getKernelType() {
        try {
            RadioGroup radioGroup = findViewById(R.id.main_kernel_group);
            int childCount = radioGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
                boolean checked = radioButton.isChecked();
                if (checked) {
                    return getResources().getIntArray(R.array.kernels_ids)[i];
                }
            }
            throw new Exception();
        } catch (Exception e) {
            return -1;
        }
    }

    private int getDecodeType() {
        try {
            RadioGroup radioGroup = findViewById(R.id.main_decoder);
            int childCount = radioGroup.getChildCount();
            for (int i = 0; i < childCount; i++) {
                RadioButton radioButton = (RadioButton) radioGroup.getChildAt(i);
                boolean checked = radioButton.isChecked();
                if (checked) {
                    return getResources().getIntArray(R.array.decoders_ids)[i];
                }
            }
            throw new Exception();
        } catch (Exception e) {
            return -1;
        }
    }

    private void initPlayer() {

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
                .setKernelType(getKernelType())
                // 渲染类型（TextuteView SurafecView）
                .setRenderType(renderType)
                // 解码器类型（仅针对 Media3Player ExoPlayer IjkPLayer）
                .setDecoderType(getDecodeType())
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
                .build();
    }
}