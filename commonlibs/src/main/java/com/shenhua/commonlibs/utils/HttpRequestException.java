package com.shenhua.commonlibs.utils;

import com.shenhua.commonlibs.mvp.HttpManager;

/**
 * Created by shenhua on 3/14/2017.
 * Email shenhuanet@126.com
 */
public class HttpRequestException extends Exception {

    private static final long serialVersionUID = -3962387110105024432L;
    private int code;
    private String msg;

    /**
     * Constructs an {@code HttpRequestException} with {@code null}
     * as its error detail message.
     */
    public HttpRequestException() {
        super();
    }

    /**
     * Constructs an {@code HttpRequestException} with the specified detail message.
     *
     * @param code    response code
     * @param message The detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     */
    public HttpRequestException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Constructs an {@code HttpRequestException} with the specified detail message
     * and cause.
     * <p>
     * <p> Note that the detail message associated with {@code cause} is
     * <i>not</i> automatically incorporated into this exception's detail
     * message.
     *
     * @param message The detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     * @param cause   The cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).  (A null value is permitted,
     *                and indicates that the cause is nonexistent or unknown.)
     * @since 1.6
     */
    public HttpRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs an {@code HttpRequestException} with the specified cause and a
     * detail message of {@code (cause==null ? null : cause.toString())}
     * (which typically contains the class and detail message of {@code cause}).
     * This constructor is useful for IO exceptions that are little more
     * than wrappers for other throwables.
     *
     * @param cause The cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).  (A null value is permitted,
     *              and indicates that the cause is nonexistent or unknown.)
     * @since 1.6
     */
    public HttpRequestException(Throwable cause) {
        super(cause);
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        switch (code) {
            case HttpManager.HTTP_STATUS_REDIRECT:
                msg = "服务器重定向";
                break;
            case HttpManager.HTTP_STATUS_FORBIDDEN:
                msg = "服务器拒绝访问";
                break;
            case HttpManager.HTTP_STATUS_NOT_FOUND:
                msg = "服务器异常，请稍后再试";
                break;
            case HttpManager.HTTP_STATUS_UNAVAILABLE:
                msg = "网络不给力";
                break;
        }
        return msg;
    }
}
