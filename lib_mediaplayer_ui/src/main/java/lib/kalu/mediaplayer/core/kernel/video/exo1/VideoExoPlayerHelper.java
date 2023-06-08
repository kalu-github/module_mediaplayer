package lib.kalu.mediaplayer.core.kernel.video.exo1;

public final class VideoExoPlayerHelper {

    private VideoExoPlayerHelper() {
    }

    private static final class Holder {
        private final static VideoExoPlayerHelper mInstance = new VideoExoPlayerHelper();
    }

    public static VideoExoPlayerHelper getInstance() {
        return Holder.mInstance;
    }
//
//    public SampleSource createMediaSource(@NonNull Context context,
//                                          @NonNull String mediaUrl,
//                                          @Nullable String subtitleUrl,
//                                          @PlayerType.CacheType int cacheType,
//                                          @NonNull int cacheMax,
//                                          @NonNull String cacheDir) {
//
//        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => mediaUrl = " + mediaUrl);
//        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => subtitleUrl = " + subtitleUrl);
//        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => cacheType = " + cacheType);
//        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => cacheMax = " + cacheMax);
//        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => cacheDir = " + cacheDir);
//
//        String scheme;
//        Uri uri = Uri.parse(mediaUrl);
//        try {
//            scheme = uri.getScheme();
//        } catch (Exception e) {
//            scheme = null;
//        }
//        MPLogUtil.log("ExoMediaSourceHelper => createMediaSource => scheme = " + scheme);
//
//        // rtmp
//        if (PlayerType.SchemeType.RTMP.equals(scheme)) {
//            MediaItem mediaItem = .fromUri(uri);
//            return new ProgressiveMediaSource.Factory(new RtmpDataSource.Factory()).createMediaSource(mediaItem);
//        }
//        // rtsp
//        else if (PlayerType.SchemeType.RTSP.equals(scheme)) {
//            MediaItem mediaItem = MediaItem.fromUri(uri);
//            return new RtspMediaSource.Factory().createMediaSource(mediaItem);
//        }
//        // other
//        else {
//            SampleSource mediaSource = create(context, mediaUrl, subtitleUrl, cacheType, cacheMax, cacheDir);
//            return mediaSource;
//        }
//    }
//
//    private SampleSource create(
//            @NonNull Context context,
//            @NonNull String mediaUrl,
//            @Nullable String subtitleUrl,
//            @PlayerType.CacheType int cacheType,
//            @NonNull int cacheMax,
//            @NonNull String cacheDir) {
//
//        String userAgent = Util.getUserAgent(context, "(Linux;Android " + Build.VERSION.RELEASE + ") ");
//        Allocator allocator = new DefaultAllocator(64 * 1024);
//        Handler mainHandler = new Handler(Looper.getMainLooper());
//        // Build the video and audio renderers.
//        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter(mainHandler, null);
//        DataSource dataSource = new DefaultUriDataSource(context, bandwidthMeter, userAgent);
//        ExtractorSampleSource sampleSource = new ExtractorSampleSource(Uri.parse(mediaUrl), dataSource, allocator,
//    }
}
