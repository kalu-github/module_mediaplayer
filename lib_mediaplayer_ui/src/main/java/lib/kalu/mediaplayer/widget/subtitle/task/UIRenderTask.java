/*
 *                       Copyright (C) of Avery
 *
 *                              _ooOoo_
 *                             o8888888o
 *                             88" . "88
 *                             (| -_- |)
 *                             O\  =  /O
 *                          ____/`- -'\____
 *                        .'  \\|     |//  `.
 *                       /  \\|||  :  |||//  \
 *                      /  _||||| -:- |||||-  \
 *                      |   | \\\  -  /// |   |
 *                      | \_|  ''\- -/''  |   |
 *                      \  .-\__  `-`  ___/-. /
 *                    ___`. .' /- -.- -\  `. . __
 *                 ."" '<  `.___\_<|>_/___.'  >'"".
 *                | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 *                \  \ `-.   \_ __\ /__ _/   .-` /  /
 *           ======`-.____`-.___\_____/___.-`____.-'======
 *                              `=- -='
 *           ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *              Buddha bless, there will never be bug!!!
 */

package lib.kalu.mediaplayer.widget.subtitle.task;

import lib.kalu.mediaplayer.widget.subtitle.engine.SubtitleEngine;
import lib.kalu.mediaplayer.widget.subtitle.model.Subtitle;
import lib.kalu.mediaplayer.widget.subtitle.runtime.AppTaskExecutor;

/**
 * @author AveryZhong.
 */

public class UIRenderTask implements Runnable {

    private Subtitle mSubtitle;
    private SubtitleEngine.OnSubtitleChangeListener mOnSubtitleChangeListener;

    public UIRenderTask(final SubtitleEngine.OnSubtitleChangeListener l) {
        mOnSubtitleChangeListener = l;
    }

    @Override
    public void run() {
        if (mOnSubtitleChangeListener != null) {
            mOnSubtitleChangeListener.onSubtitleChanged(mSubtitle);
        }
    }

    public void execute(final Subtitle subtitle) {
        mSubtitle = subtitle;
        AppTaskExecutor.mainThread().execute(this);
    }
}
