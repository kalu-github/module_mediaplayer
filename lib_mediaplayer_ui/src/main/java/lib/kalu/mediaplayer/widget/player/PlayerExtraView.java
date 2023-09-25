package lib.kalu.mediaplayer.widget.player;

import android.content.Context;

import androidx.annotation.Keep;

import lib.kalu.mediaplayer.R;
import lib.kalu.mediaplayer.widget.audio.AudioView;

@Keep
public class PlayerExtraView extends PlayerView {

    public PlayerExtraView(Context context) {
        super(context);
    }

    @Override
    protected void init() {
        super.init();
        // audio
        AudioView audioView = new AudioView(getContext().getApplicationContext());
        setTag(R.id.module_mediaplayer_id_player_audio, audioView);
    }
}