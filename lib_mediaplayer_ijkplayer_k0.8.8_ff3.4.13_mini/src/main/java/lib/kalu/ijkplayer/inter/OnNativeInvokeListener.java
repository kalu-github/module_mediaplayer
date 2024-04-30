package lib.kalu.ijkplayer.inter;

import android.os.Bundle;

public interface OnNativeInvokeListener {

    int CTRL_WILL_TCP_OPEN = 0x20001;               // NO ARGS
    int CTRL_DID_TCP_OPEN = 0x20002;                // ARG_ERROR, ARG_FAMILIY, ARG_IP, ARG_PORT, ARG_FD

    int CTRL_WILL_HTTP_OPEN = 0x20003;              // ARG_URL, ARG_SEGMENT_INDEX, ARG_RETRY_COUNTER
    int CTRL_WILL_LIVE_OPEN = 0x20005;              // ARG_URL, ARG_RETRY_COUNTER
    int CTRL_WILL_CONCAT_RESOLVE_SEGMENT = 0x20007; // ARG_URL, ARG_SEGMENT_INDEX, ARG_RETRY_COUNTER

    int EVENT_WILL_HTTP_OPEN = 0x1;                 // ARG_URL
    int EVENT_DID_HTTP_OPEN = 0x2;                  // ARG_URL, ARG_ERROR, ARG_HTTP_CODE
    int EVENT_WILL_HTTP_SEEK = 0x3;                 // ARG_URL, ARG_OFFSET
    int EVENT_DID_HTTP_SEEK = 0x4;                  // ARG_URL, ARG_OFFSET, ARG_ERROR, ARG_HTTP_CODE, ARG_FILE_SIZE

    String ARG_URL = "url";
    String ARG_SEGMENT_INDEX = "segment_index";
    String ARG_RETRY_COUNTER = "retry_counter";

    String ARG_ERROR = "error";
    String ARG_FAMILIY = "family";
    String ARG_IP = "ip";
    String ARG_PORT = "port";
    String ARG_FD = "fd";

    String ARG_OFFSET = "offset";
    String ARG_HTTP_CODE = "http_code";
    String ARG_FILE_SIZE = "file_size";

    /*
     * @return true if invoke is handled
     * @throws Exception on any error
     */
    boolean onNativeInvoke(int what, Bundle args);
}