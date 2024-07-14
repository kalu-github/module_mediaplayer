
package lib.kalu.mediaplayer.core.component;

public interface ComponentApiWarningTrySee extends ComponentApi {

    boolean[] mTrySeeFinish = new boolean[]{false};

    default void setTrySeeFinish(boolean v) {
        mTrySeeFinish[0] = v;
    }

    default boolean isTrySeeFinish() {
        return mTrySeeFinish[0];
    }

    @Override
    default boolean enableDispatchKeyEvent() {
        return true;
    }
}