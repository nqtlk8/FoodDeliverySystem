package vn.nqtlk8.voucher.context;

public class AppContextHolder {
    private static final ThreadLocal<String> traceIdHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> userIdHolder = new ThreadLocal<>();

    public static void setTraceId(String traceId) { traceIdHolder.set(traceId); }
    public static String getTraceId() { return traceIdHolder.get(); }
    public static void clearTraceId() { traceIdHolder.remove(); }

    public static void setUserId(String userId) { userIdHolder.set(userId); }
    public static String getUserId() { return userIdHolder.get(); }
    public static void clearUserId() { userIdHolder.remove(); }

    public static void clear() {
        clearTraceId();
        clearUserId();
    }
}