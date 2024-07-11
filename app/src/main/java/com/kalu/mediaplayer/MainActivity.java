package com.kalu.mediaplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
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
        findViewById(R.id.main_button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1
                initPlayer();
                // 2
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                intent.putExtra(TestActivity.INTENT_URL, getUrl());
                intent.putExtra(TestActivity.INTENT_LIVE, isLive());
                intent.putExtra(TestActivity.INTENT_SEEK, isSeek());
                intent.putExtra(TestActivity.INTENT_TRY_SEE, isTrySee());
                intent.putExtra(TestActivity.INTENT_EPISODE, isEpisode());
                intent.putExtra(TestActivity.INTENT_EPISODE_PLAY_INDEX, 1);
                intent.putExtra(TestActivity.INTENT_EPISODE_ITEM_COUNT, 4);
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

    private boolean isSeek() {
        RadioGroup radioGroup = findViewById(R.id.main_seek);
        return R.id.main_seek_yes == radioGroup.getCheckedRadioButtonId();
    }

    private boolean isEpisode() {
        RadioGroup radioGroup = findViewById(R.id.main_episode);
        return R.id.main_episode_yes == radioGroup.getCheckedRadioButtonId();
    }

    private boolean isTrySee() {
        RadioGroup radioGroup = findViewById(R.id.main_trysee);
        return R.id.main_trysee_yes == radioGroup.getCheckedRadioButtonId();
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

        // 1
        int kernelType;
        int kernelTypeId = ((RadioGroup) findViewById(R.id.main_kernel)).getCheckedRadioButtonId();
        switch (kernelTypeId) {
            case R.id.main_kernel_ijk:
                kernelType = PlayerType.KernelType.IJK;
                break;
            case R.id.main_kernel_ijk_mediacodec:
                kernelType = PlayerType.KernelType.IJK_MEDIACODEC;
                break;
            case R.id.main_kernel_exo_v1:
                kernelType = PlayerType.KernelType.EXO_V1;
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
            case R.id.main_kernel_mediax:
                kernelType = PlayerType.KernelType.MEDIA_V3;
                break;
            default:
                kernelType = PlayerType.KernelType.ANDROID;
                break;
        }

        int exoFFmpeg = 0;
        int exoFFmpegId = ((RadioGroup) findViewById(R.id.main_exo_ffmpeg)).getCheckedRadioButtonId();
        switch (exoFFmpegId) {
            case R.id.main_exo_vff_amc:
                exoFFmpeg = PlayerType.ExoFFmpegType.EXO_RENDERER_VIDEO_FFMPEG_AUDIO_MEDIACODEC;
                break;
            case R.id.main_exo_vmc_aff:
                exoFFmpeg = PlayerType.ExoFFmpegType.EXO_RENDERER_VIDEO_MEDIACODEC_AUDIO_FFMPEG;
                break;
            case R.id.main_exo_vff_aff:
                exoFFmpeg = PlayerType.ExoFFmpegType.EXO_RENDERER_ONLY_FFMPEG;
                break;
            default:
                exoFFmpeg = PlayerType.ExoFFmpegType.EXO_RENDERER_ONLY_MEDIACODEC;
                break;
        }

        int renderType;
        int renderTypeId = ((RadioGroup) findViewById(R.id.main_render)).getCheckedRadioButtonId();
        switch (renderTypeId) {
            case R.id.main_render_surfaceview:
                renderType = PlayerType.RenderType.SURFACE_VIEW;
                break;
            default:
                renderType = PlayerType.RenderType.TEXTURE_VIEW;
                break;
        }

        int scaleType;
        int scaleTypeId = ((RadioGroup) findViewById(R.id.main_scale)).getCheckedRadioButtonId();
        switch (scaleTypeId) {
            case R.id.main_scale2:
                scaleType = PlayerType.ScaleType.SCREEN_SCALE_ORIGINAL;
                break;
            case R.id.main_scale4:
                scaleType = PlayerType.ScaleType.SCREEN_SCALE_CROP;
                break;
            case R.id.main_scale5:
                scaleType = PlayerType.ScaleType.SCREEN_SCALE_4_3;
                break;
            case R.id.main_scale6:
                scaleType = PlayerType.ScaleType.SCREEN_SCALE_16_9;
                break;
            default:
                scaleType = PlayerType.ScaleType.SCREEN_SCALE_MATCH;
                break;
        }

        boolean exoUseOkhttp;
        int httpTypeId = ((RadioGroup) findViewById(R.id.main_exo_http)).getCheckedRadioButtonId();
        switch (httpTypeId) {
            case R.id.main_exo_http_okhttp:
                exoUseOkhttp = true;
                break;
            default:
                exoUseOkhttp = false;
                break;
        }

        boolean cacheFlag;
        int cacheFlagId = ((RadioGroup) findViewById(R.id.main_cache)).getCheckedRadioButtonId();
        switch (cacheFlagId) {
            case R.id.main_cache_yes:
                cacheFlag = true;
                break;
            default:
                cacheFlag = false;
                break;
        }

        Log.e("MainActivity", "initPlayer => kernelType = " + kernelType + ", renderType = " + renderType + ", exoFFmpeg = " + exoFFmpeg + ", scaleType = " + scaleType + ", exoUseOkhttp = " + exoUseOkhttp);
        PlayerSDK.init()
                .setLog(true)
                .setKernel(kernelType)
                .setRender(renderType)
                .setScaleType(scaleType)
                .setExoFFmpeg(exoFFmpeg)
                .setExoUseOkhttp(exoUseOkhttp).setExoCacheType(cacheFlag ? PlayerType.CacheType.DOWNLOAD : PlayerType.CacheType.NONE)
                .setBuriedEvent(new LogBuriedEvent())
                .build();
    }
}