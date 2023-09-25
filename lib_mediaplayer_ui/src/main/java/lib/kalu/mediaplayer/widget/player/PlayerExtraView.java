//package lib.kalu.mediaplayer.widget.player;
//
//import android.content.Context;
//
//import androidx.annotation.Keep;
//import androidx.annotation.NonNull;
//
//import lib.kalu.mediaplayer.core.kernel.audio.AudioKernelApi;
//import lib.kalu.mediaplayer.core.player.audio.AudioPlayerApi;
//import lib.kalu.mediaplayer.listener.OnPlayerAudioChangeListener;
//
//@Keep
//public class PlayerExtraView extends PlayerView implements AudioPlayerApi {
//
//    // 解码
//    protected AudioKernelApi mAudioKernelApi;
//
//    public PlayerExtraView(Context context) {
//        super(context);
//    }
//
//    @Override
//    public void setOnPlayerChangeListener(@NonNull OnPlayerAudioChangeListener l) {
//        AudioPlayerApi.super.setOnPlayerChangeListener(l);
//    }
//
//    @Override
//    public AudioKernelApi getAudioKernel() {
//        return mAudioKernelApi;
//    }
//
//    @Override
//    public void setAudioKernel(@NonNull AudioKernelApi kernel) {
//        this.mAudioKernelApi = kernel;
//    }
//}