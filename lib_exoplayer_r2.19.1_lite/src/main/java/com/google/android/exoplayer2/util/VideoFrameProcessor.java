/*
 * Copyright 2022 The Android Open Source Project
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
package com.google.android.exoplayer2.util;

import static java.lang.annotation.ElementType.TYPE_USE;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.EGLExt;
import android.view.Surface;
import androidx.annotation.IntDef;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.video.ColorInfo;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Interface for a video frame processor that applies changes to individual video frames.
 *
 * <p>The changes are specified by {@link Effect} instances passed to {@link Factory#create}.
 *
 * <p>Manages its input {@link Surface}, which can be accessed via {@link #getInputSurface()}. The
 * output {@link Surface} must be set by the caller using {@link
 * #setOutputSurfaceInfo(SurfaceInfo)}.
 *
 * <p>The caller must {@linkplain #registerInputFrame() register} input frames before rendering them
 * to the input {@link Surface}.
 *
 * @deprecated com.google.android.exoplayer2 is deprecated. Please migrate to androidx.media3 (which
 *     contains the same ExoPlayer code). See <a
 *     href="https://developer.android.com/guide/topics/media/media3/getting-started/migration-guide">the
 *     migration guide</a> for more details, including a script to help with the migration.
 */
@Deprecated
public interface VideoFrameProcessor {
  // TODO(b/243036513): Allow effects to be replaced.

  /**
   * Specifies how the input frames are made available to the {@link VideoFrameProcessor}. One of
   * {@link #INPUT_TYPE_SURFACE}, {@link #INPUT_TYPE_BITMAP} or {@link #INPUT_TYPE_TEXTURE_ID}.
   */
  @Documented
  @Retention(RetentionPolicy.SOURCE)
  @Target(TYPE_USE)
  @IntDef({INPUT_TYPE_SURFACE, INPUT_TYPE_BITMAP, INPUT_TYPE_TEXTURE_ID})
  @interface InputType {}
  /** Input frames come from a {@link #getInputSurface surface}. */
  int INPUT_TYPE_SURFACE = 1;
  /** Input frames come from a {@link Bitmap}. */
  int INPUT_TYPE_BITMAP = 2;
  /**
   * Input frames come from a {@linkplain android.opengl.GLES10#GL_TEXTURE_2D traditional GLES
   * texture}.
   */
  int INPUT_TYPE_TEXTURE_ID = 3;

  /** A factory for {@link VideoFrameProcessor} instances. */
  interface Factory {

    // TODO(271433904): Turn parameters with default values into setters.
    /**
     * Creates a new {@link VideoFrameProcessor} instance.
     *
     * @param context A {@link Context}.
     * @param effects The {@link Effect} instances to apply to each frame. Applied on the {@code
     *     outputColorInfo}'s color space.
     * @param debugViewProvider A {@link DebugViewProvider}.
     * @param inputColorInfo The {@link ColorInfo} for the input frames.
     * @param outputColorInfo The {@link ColorInfo} for the output frames.
     * @param renderFramesAutomatically If {@code true}, the instance will render output frames to
     *     the {@linkplain #setOutputSurfaceInfo(SurfaceInfo) output surface} automatically as
     *     {@link VideoFrameProcessor} is done processing them. If {@code false}, the {@link
     *     VideoFrameProcessor} will block until {@link #renderOutputFrame(long)} is called, to
     *     render or drop the frame.
     * @param listenerExecutor The {@link Executor} on which the {@code listener} is invoked.
     * @param listener A {@link Listener}.
     * @return A new instance.
     * @throws VideoFrameProcessingException If a problem occurs while creating the {@link
     *     VideoFrameProcessor}.
     */
    VideoFrameProcessor create(
        Context context,
        List<Effect> effects,
        DebugViewProvider debugViewProvider,
        ColorInfo inputColorInfo,
        ColorInfo outputColorInfo,
        boolean renderFramesAutomatically,
        Executor listenerExecutor,
        Listener listener)
        throws VideoFrameProcessingException;
  }

  /**
   * Listener for asynchronous frame processing events.
   *
   * <p>All listener methods must be called from the {@link Executor} passed in at {@linkplain
   * Factory#create creation}.
   */
  interface Listener {

    /**
     * Called when the output size changes.
     *
     * <p>The output size is the frame size in pixels after applying all {@linkplain Effect
     * effects}.
     *
     * <p>The output size may differ from the size specified using {@link
     * #setOutputSurfaceInfo(SurfaceInfo)}.
     */
    void onOutputSizeChanged(int width, int height);

    /**
     * Called when an output frame with the given {@code presentationTimeUs} becomes available for
     * rendering.
     *
     * @param presentationTimeUs The presentation time of the frame, in microseconds.
     */
    void onOutputFrameAvailableForRendering(long presentationTimeUs);

    /**
     * Called when an exception occurs during asynchronous video frame processing.
     *
     * <p>If an error occurred, consuming and producing further frames will not work as expected and
     * the {@link VideoFrameProcessor} should be released.
     */
    void onError(VideoFrameProcessingException exception);

    /** Called after the {@link VideoFrameProcessor} has rendered its final output frame. */
    void onEnded();
  }

  /**
   * Indicates the frame should be rendered immediately after {@link #renderOutputFrame(long)} is
   * invoked.
   */
  long RENDER_OUTPUT_FRAME_IMMEDIATELY = -1;

  /** Indicates the frame should be dropped after {@link #renderOutputFrame(long)} is invoked. */
  long DROP_OUTPUT_FRAME = -2;

  /**
   * Provides an input {@link Bitmap} to the {@link VideoFrameProcessor}.
   *
   * <p>Can be called on any thread.
   *
   * @param inputBitmap The {@link Bitmap} queued to the {@code VideoFrameProcessor}.
   * @param durationUs The duration for which to display the {@code inputBitmap}, in microseconds.
   * @param frameRate The frame rate at which to display the {@code inputBitmap}, in frames per
   *     second.
   * @throws UnsupportedOperationException If the {@code VideoFrameProcessor} does not accept
   *     {@linkplain #INPUT_TYPE_BITMAP bitmap input}.
   */
  // TODO(b/262693274): Remove duration and frameRate parameters when EditedMediaItem can be
  //  signalled down to the processors.
  void queueInputBitmap(Bitmap inputBitmap, long durationUs, float frameRate);

  /**
   * Provides an input texture ID to the {@code VideoFrameProcessor}.
   *
   * <p>It must be called after the {@link #setOnInputFrameProcessedListener
   * onInputFrameProcessedListener} and the {@link #setInputFrameInfo frameInfo} have been set.
   *
   * <p>Can be called on any thread.
   *
   * @param textureId The ID of the texture queued to the {@code VideoFrameProcessor}.
   * @param presentationTimeUs The presentation time of the queued texture, in microseconds.
   */
  void queueInputTexture(int textureId, long presentationTimeUs);

  /**
   * Sets the {@link OnInputFrameProcessedListener}.
   *
   * <p>Can be called on any thread.
   *
   * @param listener The {@link OnInputFrameProcessedListener}.
   */
  void setOnInputFrameProcessedListener(OnInputFrameProcessedListener listener);

  /**
   * Returns the input {@link Surface}, where {@link VideoFrameProcessor} consumes input frames
   * from.
   *
   * <p>Can be called on any thread.
   *
   * @throws UnsupportedOperationException If the {@code VideoFrameProcessor} does not accept
   *     {@linkplain #INPUT_TYPE_SURFACE surface input}.
   */
  Surface getInputSurface();

  /**
   * Informs the {@code VideoFrameProcessor} that a new input stream will be queued.
   *
   * <p>Call {@link #setInputFrameInfo} before this method if the {@link FrameInfo} of the new input
   * stream differs from that of the current input stream.
   */
  // TODO(b/274109008) Merge this and setInputFrameInfo.
  void registerInputStream(@InputType int inputType);

  /**
   * Sets information about the input frames.
   *
   * <p>The new input information is applied from the next frame {@linkplain #registerInputFrame()
   * registered} or {@linkplain #queueInputTexture} queued} onwards.
   *
   * <p>Pixels are expanded using the {@link FrameInfo#pixelWidthHeightRatio} so that the output
   * frames' pixels have a ratio of 1.
   *
   * <p>Can be called on any thread.
   */
  void setInputFrameInfo(FrameInfo inputFrameInfo);

  /**
   * Informs the {@code VideoFrameProcessor} that a frame will be queued to its {@linkplain
   * #getInputSurface() input surface}.
   *
   * <p>Must be called before rendering a frame to the input surface.
   *
   * <p>Can be called on any thread.
   *
   * @throws UnsupportedOperationException If the {@code VideoFrameProcessor} does not accept
   *     {@linkplain #INPUT_TYPE_SURFACE surface input}.
   * @throws IllegalStateException If called after {@link #signalEndOfInput()} or before {@link
   *     #setInputFrameInfo(FrameInfo)}.
   */
  void registerInputFrame();

  /**
   * Returns the number of input frames that have been made available to the {@code
   * VideoFrameProcessor} but have not been processed yet.
   *
   * <p>Can be called on any thread.
   */
  int getPendingInputFrameCount();

  /**
   * Sets the output surface and supporting information. When output frames are rendered and not
   * dropped, they will be rendered to this output {@link SurfaceInfo}.
   *
   * <p>The new output {@link SurfaceInfo} is applied from the next output frame rendered onwards.
   * If the output {@link SurfaceInfo} is {@code null}, the {@code VideoFrameProcessor} will stop
   * rendering pending frames and resume rendering once a non-null {@link SurfaceInfo} is set.
   *
   * <p>If the dimensions given in {@link SurfaceInfo} do not match the {@linkplain
   * Listener#onOutputSizeChanged(int,int) output size after applying the final effect} the frames
   * are resized before rendering to the surface and letter/pillar-boxing is applied.
   *
   * <p>The caller is responsible for tracking the lifecycle of the {@link SurfaceInfo#surface}
   * including calling this method with a new surface if it is destroyed. When this method returns,
   * the previous output surface is no longer being used and can safely be released by the caller.
   */
  void setOutputSurfaceInfo(@Nullable SurfaceInfo outputSurfaceInfo);

  /**
   * Renders the oldest unrendered output frame that has become {@linkplain
   * Listener#onOutputFrameAvailableForRendering(long) available for rendering} at the given {@code
   * renderTimeNs}.
   *
   * <p>This will either render the output frame to the {@linkplain #setOutputSurfaceInfo output
   * surface}, or drop the frame, per {@code renderTimeNs}.
   *
   * <p>This method must only be called if {@code renderFramesAutomatically} was set to {@code
   * false} using the {@link Factory} and should be called exactly once for each frame that becomes
   * {@linkplain Listener#onOutputFrameAvailableForRendering(long) available for rendering}.
   *
   * <p>The {@code renderTimeNs} may be passed to {@link EGLExt#eglPresentationTimeANDROID}
   * depending on the implementation.
   *
   * @param renderTimeNs The render time to use for the frame, in nanoseconds. The render time can
   *     be before or after the current system time. Use {@link #DROP_OUTPUT_FRAME} to drop the
   *     frame, or {@link #RENDER_OUTPUT_FRAME_IMMEDIATELY} to render the frame immediately.
   */
  void renderOutputFrame(long renderTimeNs);

  /**
   * Informs the {@code VideoFrameProcessor} that no further input frames should be accepted.
   *
   * <p>Can be called on any thread.
   *
   * @throws IllegalStateException If called more than once.
   */
  void signalEndOfInput();

  /**
   * Flushes the {@code VideoFrameProcessor}.
   *
   * <p>All the frames that are {@linkplain #registerInputFrame() registered} prior to calling this
   * method are no longer considered to be registered when this method returns.
   *
   * <p>{@link Listener} methods invoked prior to calling this method should be ignored.
   *
   * @throws UnsupportedOperationException If the {@code VideoFrameProcessor} does not accept
   *     {@linkplain #INPUT_TYPE_SURFACE surface input}.
   */
  void flush();

  /**
   * Releases all resources.
   *
   * <p>If the {@code VideoFrameProcessor} is released before it has {@linkplain Listener#onEnded()
   * ended}, it will attempt to cancel processing any input frames that have already become
   * available. Input frames that become available after release are ignored.
   *
   * <p>This method blocks until all resources are released or releasing times out.
   *
   * <p>Can be called on any thread.
   */
  void release();
}
