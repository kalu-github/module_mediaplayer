package lib.kalu.mediaplayer.core.component;

public interface ComponentApiWarningPlayInfo extends ComponentApi {

    boolean[] mShowPlayInfoRecord = new boolean[]{false};

    default void setComponentShowPlayInfoRecord(boolean v) {
        mShowPlayInfoRecord[0] = v;
    }

    default boolean isShowWarningPlayInfoRecord() {
        return mShowPlayInfoRecord[0];
    }
}