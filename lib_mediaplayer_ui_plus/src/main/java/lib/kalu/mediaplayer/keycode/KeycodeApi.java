package lib.kalu.mediaplayer.keycode;

import androidx.annotation.Keep;

/**
 * https://www.cnblogs.com/bluestorm/p/4886662.html
 * description:
 * created by kalu on 2021/9/27
 */
@Keep
public interface KeycodeApi {

    int pause();

    int play();

    int fastForward();

    int fastRewind();

    int volumeUp();

    int volumeDown();

    int search();

    int back();

    int home();

    int menu();

    int dpadLeft();

    int dpadRight();

    int dpadUp();

    int dpadDown();

    int dpadCenter();
}
