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

import androidx.annotation.Nullable;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.TransferListener;

/**
 * @deprecated Use {@link RtmpDataSource.Factory} instead.
 */
@Deprecated
@UnstableApi
public final class RtmpDataSourceFactory implements DataSource.Factory {

  @Nullable private final TransferListener listener;

  public RtmpDataSourceFactory() {
    this(null);
  }

  /**
   * @param listener An optional listener.
   */
  public RtmpDataSourceFactory(@Nullable TransferListener listener) {
    this.listener = listener;
  }

  @Override
  public RtmpDataSource createDataSource() {
    RtmpDataSource dataSource = new RtmpDataSource();
    if (listener != null) {
      dataSource.addTransferListener(listener);
    }
    return dataSource;
  }
}
