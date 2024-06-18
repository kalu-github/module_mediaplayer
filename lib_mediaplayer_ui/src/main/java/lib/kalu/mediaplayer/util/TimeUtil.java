package lib.kalu.mediaplayer.util;

public final class TimeUtil {

    public static String formatTimeMillis(long v) {
        if (v <= 3600000) {
            return formatTimeMillis1(v);
        } else {
            return formatTimeMillis2(v);
        }
    }

    public static String formatTimeMillis(long v, long max) {
        if (max <= 3600000) {
            return formatTimeMillis1(v);
        } else {
            return formatTimeMillis2(v);
        }
    }

    private static String formatTimeMillis1(long v) {
        try {
            if (v < 1000)
                throw new Exception();
            StringBuilder builder = new StringBuilder();
            // min
            long min = v / 60000;
            if (min < 10) {
                builder.append("0");
            }
            builder.append(min);
            builder.append(":");
            // second
            long second = (v % 60000) / 1000;
            if (second < 10) {
                builder.append("0");
            }
            builder.append(second);
            return builder.toString();
        } catch (Exception e) {
            return "00:00";
        }
    }

    private static String formatTimeMillis2(long v) {
        try {
            if (v < 1000)
                throw new Exception();
            StringBuilder builder = new StringBuilder();
            // hour
            long hour = v / 3600000;
            if (hour < 10) {
                builder.append("0");
            }
            builder.append(hour);
            builder.append(":");
            // min
            long min = (v % 3600000) / 60000;
            if (min < 10) {
                builder.append("0");
            }
            builder.append(min);
            builder.append(":");
            // second
            long second = ((v % 3600000) % 60000) / 1000;
            if (second < 10) {
                builder.append("0");
            }
            builder.append(second);
            return builder.toString();
        } catch (Exception e) {
            return "00:00:00";
        }
    }
}
