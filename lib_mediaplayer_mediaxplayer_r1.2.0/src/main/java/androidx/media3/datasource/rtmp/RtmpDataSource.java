/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.media3.datasource.rtmp;

import static androidx.media3.common.util.Util.castNonNull;

import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.media3.common.C;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.BaseDataSource;
import androidx.media3.datasource.DataSpec;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import java.io.IOException;

@UnstableApi
public final class RtmpDataSource extends BaseDataSource {

    static {
//        ExoPlayerLibraryInfo.registerModule("goog.exo.rtmp");
    }

    public RtmpDataSource() {
        super(/* isNetwork= */ true);
    }

    @Override
    public long open(DataSpec dataSpec)  {
        return C.LENGTH_UNSET;
    }

    @Override
    public int read(byte[] buffer, int offset, int length) throws IOException {
        return 0;
    }

    @Override
    public void close() {
    }

    @Override
    @Nullable
    public Uri getUri() {
        return null;
    }
}
