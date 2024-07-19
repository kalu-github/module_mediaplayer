package lib.kalu.mediaplayer.buried;


import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

import lib.kalu.mediaplayer.args.StartArgs;
import lib.kalu.mediaplayer.type.PlayerType;

public interface BuriedEvent extends Serializable {

    void onRendering(@NotNull StartArgs args, long position, long duration);

    void onStart(@NotNull StartArgs args, long position, long duration);

    void onError(@NotNull StartArgs args, long position, long duration, @PlayerType.EventType.Value int code);

    void onPause(@NotNull StartArgs args, long position, long duration);

    void onResume(@NotNull StartArgs args, long position, long duration);

    void onComplete(@NotNull StartArgs args, long position, long duration);

    void onStop(@NotNull StartArgs args, long position, long duration);

    void onBufferingStart(@NotNull StartArgs args, long position, long duration);

    void onBufferingStop(@NotNull StartArgs args, long position, long duration);

    void onSeekStart(@NotNull StartArgs args, long position, long duration);

    void onSeekFinish(@NotNull StartArgs args, long position, long duration);

    void onWindow(@NotNull StartArgs args, long position, long duration, @PlayerType.WindowType.Value int type);
}