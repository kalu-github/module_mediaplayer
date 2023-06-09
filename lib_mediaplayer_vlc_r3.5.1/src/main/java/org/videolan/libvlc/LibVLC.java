/*****************************************************************************
 * LibVLC.java
 *****************************************************************************
 * Copyright © 2010-2013 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 *****************************************************************************/

package org.videolan.libvlc;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import org.videolan.libvlc.interfaces.AbstractVLCEvent;
import org.videolan.libvlc.interfaces.ILibVLC;
import org.videolan.libvlc.util.HWDecoderUtil;

import java.util.ArrayList;
import java.util.List;

import lib.kalu.vlc.util.VlcLogUtil;

public class LibVLC extends VLCObject<ILibVLC.Event> implements ILibVLC {

    final Context mAppContext;

    static {
        try {
            System.loadLibrary("c++_shared");
            System.loadLibrary("vlc");
            System.loadLibrary("vlcjni");
        } catch (UnsatisfiedLinkError ule) {
            VlcLogUtil.log("LibVLC => Can't load vlcjni library: " + ule);
        } catch (SecurityException se) {
            VlcLogUtil.log("LibVLC => Encountered a security issue when loading vlcjni library: " + se);
        }
    }

    public static class Event extends AbstractVLCEvent {
        protected Event(int type) {
            super(type);
        }
    }

    public LibVLC(Context context, List<String> options) {
        mAppContext = context.getApplicationContext();

        if (options == null)
            options = new ArrayList<>();
        boolean setAout = true, setChroma = true;
        // check if aout/vout options are already set
        for (String option : options) {
            if (option.startsWith("--aout="))
                setAout = false;
            if (option.startsWith("--android-display-chroma"))
                setChroma = false;
            if (!setAout && !setChroma)
                break;
        }

        // set aout/vout options if they are not set
        if (setAout || setChroma) {
            if (setAout) {
                final HWDecoderUtil.AudioOutput hwAout = HWDecoderUtil.getAudioOutputFromDevice();
                if (hwAout == HWDecoderUtil.AudioOutput.OPENSLES)
                    options.add("--aout=opensles");
                else
                    options.add("--aout=android_audiotrack");
            }
            if (setChroma) {
                options.add("--android-display-chroma");
                options.add("RV16");
            }
        }
        nativeNew(options.toArray(new String[options.size()]), context.getDir("vlc", Context.MODE_PRIVATE).getAbsolutePath());
    }

    /**
     * Create a LibVLC
     */
    public LibVLC(Context context) {
        this(context, null);
    }

    @Override
    protected ILibVLC.Event onEventNative(int eventType, long arg1, long arg2, float argf1, @Nullable String args1) {
        return null;
    }

    @Override
    public Context getAppContext() {
        return mAppContext;
    }

    @Override
    protected void onReleaseNative() {
        nativeRelease();
    }

    public void setUserAgent(String name, String http) {
        nativeSetUserAgent(name, http);
    }

    /* JNI */
    private native void nativeNew(String[] options, String homePath);

    private native void nativeRelease();

    private native void nativeSetUserAgent(String name, String http);

    /**
     * Get the libVLC version
     *
     * @return the libVLC version string
     */
    public static native String version();

    /**
     * Get the libVLC major version
     *
     * @return the libVLC major version, always >= 3
     */
    public static native int majorVersion();

    /**
     * Get the libVLC compiler
     *
     * @return the libVLC compiler string
     */
    public static native String compiler();

    /**
     * Get the libVLC changeset
     *
     * @return the libVLC changeset string
     */
    public static native String changeset();
}
