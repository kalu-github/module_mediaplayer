package lib.kalu.mediaplayer.core.component;

public interface ComponentApiPause extends ComponentApi {
    @Override
    default boolean enableDispatchKeyEvent() {
        return true;
    }
}