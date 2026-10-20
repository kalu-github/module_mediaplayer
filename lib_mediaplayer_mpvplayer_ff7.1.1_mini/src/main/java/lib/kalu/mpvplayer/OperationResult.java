package lib.kalu.mpvplayer;

/** ACCEPTED means pending; only the subsequent track result confirms selection. */
public final class OperationResult {
    public enum Status { APPLIED, ACCEPTED, UNSUPPORTED, INVALID_STATE, INVALID_ARGUMENT, FAILED, CANCELLED }
    public final Status status;
    public final String message;
    public final long requestId;
    private OperationResult(Status status, String message, long requestId) {
        this.status = status; this.message = message; this.requestId = requestId;
    }
    public boolean isSuccess() { return status == Status.APPLIED || status == Status.ACCEPTED; }
    static OperationResult of(Status status, String message) { return of(status, message, 0); }
    static OperationResult of(Status status, String message, long id) { return new OperationResult(status, message, id); }
    @Override public String toString() { return status + ": " + message; }
}
