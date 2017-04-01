package com.shenhua.commonlibs.mvp;

import android.content.Context;

import com.shenhua.commonlibs.utils.HttpRequestException;
import com.shenhua.commonlibs.utils.NetworkUtils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Shenhua on 2/15/2017.
 * e-mail shenhuanet@126.com
 */
public class HttpManager {

    private static HttpManager instance = null;
    private static final String CACHE_DIR = "myCache";
    public static final String USER_AGENT = "MQQBrowser/26 Mozilla/5.0 (Linux; U; Android 2.3.7; zh-cn; MB200 Build/GRJ22; CyanogenMod-7) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
    public static final String CONNECTION = "keep-alive";
    public static final int HTTP_STATUS_OK = 200;
    public static final int HTTP_STATUS_REDIRECT = 302;
    public static final int HTTP_STATUS_FORBIDDEN = 403;
    public static final int HTTP_STATUS_NOT_FOUND = 404;
    public static final int HTTP_STATUS_UNAVAILABLE = 504;
    private Retrofit retrofit;
    private static volatile OkHttpClient okHttpClient;
    private static ExecutorService executorService;

    public static HttpManager getInstance() {
        if (instance == null) {
            instance = new HttpManager();
        }
        return instance;
    }

    public Retrofit getRetrofit(Context context, String baseUrl) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(getOkHttpClient(context))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * Send http asynchronous request
     *
     * @param runnable runnable in thread
     */
    public void sendRequest(Runnable runnable) {
        if (executorService == null)
            executorService = Executors.newSingleThreadExecutor();
        if (runnable != null)
            executorService.submit(runnable);
    }

    public OkHttpClient getOkHttpClient(Context context) {
        return this.getOkHttpClient(context, true, true);
    }

    public OkHttpClient getOkHttpClient(Context context, boolean followRedirects) {
        return this.getOkHttpClient(context, true, followRedirects);
    }

    public OkHttpClient getOkHttpClient(Context context, boolean useLog, boolean followRedirects) {
        if (okHttpClient == null) {
            synchronized (HttpManager.class) {
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                if (useLog) {
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    builder.addInterceptor(loggingInterceptor);
                }
                File cacheDir = new File(context.getExternalCacheDir(), CACHE_DIR);
                int cacheSize = 10 * 1024 * 1024; //10MB
                Cache cache = new Cache(cacheDir, cacheSize);
                builder.followRedirects(followRedirects);
                builder.cache(cache);
                builder.addInterceptor(new RewriteCacheControlInterceptor(context));
                okHttpClient = builder.build();
            }
        }
        return okHttpClient;
    }

    public OkHttpClient getOkHttpClientSaveCookies(Context context, boolean useLog) {
        if (okHttpClient == null) {
            synchronized (HttpManager.class) {
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                if (useLog) {
                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                    builder.addInterceptor(loggingInterceptor);
                }
                File cacheDir = new File(context.getExternalCacheDir(), CACHE_DIR);
                int cacheSize = 10 * 1024 * 1024; //10MB
                Cache cache = new Cache(cacheDir, cacheSize);
                builder.cache(cache);
                builder.addInterceptor(new RewriteCacheControlInterceptor(context));
                builder.cookieJar(new CookieJar() {

                    private final HashMap<HttpUrl, List<Cookie>> cookieStore = new HashMap<>();

                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                        cookieStore.put(url, cookies);
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        List<Cookie> cookies = cookieStore.get(url);
                        return cookies != null ? cookies : new ArrayList<Cookie>();
                    }
                });
                okHttpClient = builder.build();
            }
        }
        return okHttpClient;
    }

    private class RewriteCacheControlInterceptor implements Interceptor {

        Context context;

        RewriteCacheControlInterceptor(Context context) {
            this.context = context;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            CacheControl.Builder cb = new CacheControl.Builder();
            cb.maxAge(0, TimeUnit.SECONDS);
            cb.maxStale(365, TimeUnit.DAYS);
            CacheControl cacheControl = cb.build();
            Request request = chain.request();
            if (!NetworkUtils.isConnectedNet(context)) {
                request = request.newBuilder().cacheControl(cacheControl).build();
            }
            Response originalResponse = chain.proceed(request);
            if (NetworkUtils.isConnectedNet(context)) {
                int maxAge = 0; // read from cache
                return originalResponse.newBuilder().removeHeader("Pragma")
                        .header("Cache-Control", "public,max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 7;// 7days
                return originalResponse.newBuilder().removeHeader("Pragma")
                        .header("Cache-Control", "public,only-if-xcached,max-stale=" + maxStale)
                        .build();
            }
        }
    }

    /**
     * create a Html Get Observable
     *
     * @param context context
     * @param url     url
     * @return Observable
     */
    public Observable createHtmlGetObservable(Context context, String url) {
        return this.createHtmlGetObservable(context, url, null, true);
    }

    /**
     * Create a Html Get Observable
     *
     * @param context context
     * @param url     url
     * @param charset charset
     * @return Observable
     */
    public Observable createHtmlGetObservable(Context context, String url, String charset) {
        return this.createHtmlGetObservable(context, url, charset, true);
    }

    /**
     * Create a Html Get Observable
     *
     * @param context context
     * @param url     url
     * @param useLog  whether to print the request log
     * @return Observable
     */
    public Observable createHtmlGetObservable(Context context, String url, boolean useLog) {
        return this.createHtmlGetObservable(context, url, null, useLog);
    }

    public Observable createHtmlGetObservable(final Context context, final String url, final String charset, final boolean useLog) {
        return createHtmlGetObservable(context, url, charset, useLog, true);
    }

    /**
     * create a Html Get Observable
     *
     * @param context context
     * @param url     url
     * @param charset charset
     * @param useLog  whether to print the request log
     * @return Observable
     */
    public Observable createHtmlGetObservable(final Context context, final String url, final String charset, final boolean useLog, final boolean followRedirects) {
        return Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onStart();
                Request request = new Request.Builder().url(url).build();
                Call call = getOkHttpClient(context, useLog, followRedirects).newCall(request);
                try {
                    Response response = call.execute();
                    int statusCode = response.code();
                    switch (statusCode) {
                        case HTTP_STATUS_OK:
                            subscriber.onNext(charset == null
                                    ? response.body().string()
                                    : new String(response.body().bytes(), charset));
                            break;
                        case HTTP_STATUS_REDIRECT:// 重定向
                        case HTTP_STATUS_FORBIDDEN:
                        case HTTP_STATUS_NOT_FOUND:
                        case HTTP_STATUS_UNAVAILABLE:
                            subscriber.onError(new HttpRequestException(statusCode, "error"));
                            break;
                        default:
                            subscriber.onNext(charset == null
                                    ? response.body().string()
                                    : new String(response.body().bytes(), charset));
                            break;
                    }
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    public Observable createHtmlPostObservable(final Context context, final String url, final RequestBody formBody) {
        return createHtmlPostObservable(context, url, "utf-8", formBody);
    }

    public Observable createHtmlPostObservable(final Context context, final String url, final String charset, final RequestBody formBody) {
        return createHtmlPostObservable(context, url, charset, formBody, false);
    }

    public Observable createHtmlPostObservable(final Context context, final String url, final String charset, final RequestBody formBody, final boolean useLog) {
        return createHtmlPostObservable(context, url, charset, formBody, useLog, true);
    }

    public Observable createHtmlPostObservable(final Context context, final String url, final String charset, final RequestBody formBody, final boolean useLog, final boolean followRedirects) {
        return Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onStart();
                Request request = new Request.Builder().url(url).post(formBody).build();
                Call call = getOkHttpClient(context, useLog, followRedirects).newCall(request);
                try {
                    Response response = call.execute();
                    String result = new String(response.body().bytes(), charset);
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    public Connection getConnection(String host, String param, Connection.Method method) {
        return Jsoup.connect(host + param)
                .header("Host", host.replace("http://", ""))
                .header("Connection", CONNECTION)
                .timeout(30000)
                .header("User-Agent", USER_AGENT)
                .method(method)
                .followRedirects(false);
    }

    public Connection.Response buildResponse(Connection connection) {
        try {
            return connection.execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Response doResponseGet(Context context, String url) {
        return doResponseGet(context, url, true);
    }

    public Response doResponseGet(Context context, String url, boolean followRedirects) {
        Request request = new Request.Builder().url(url).get().build();
        Call call = getOkHttpClient(context, followRedirects).newCall(request);
        try {
            return call.execute();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String doGet(Context context, String url) {
        return doGet(context, url, "utf-8", true);
    }

    public String doGet(Context context, String url, boolean followRedirects) {
        return doGet(context, url, "utf-8", followRedirects);
    }

    public String doGet(Context context, String url, String charset, boolean followRedirects) {
        Request request = new Request.Builder().url(url).get().build();
        Call call = getOkHttpClient(context, followRedirects).newCall(request);
        try {
            Response response = call.execute();
            return new String(response.body().bytes(), charset);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String doPost(Context context, String url, RequestBody formBody) {
        return doPost(context, url, formBody, "utf-8", true);
    }

    public String doPost(Context context, String url, RequestBody formBody, String charset, boolean followRedirects) {
        Request request = new Request.Builder().url(url).post(formBody).build();
        Call call = getOkHttpClient(context, followRedirects).newCall(request);
        try {
            Response response = call.execute();
            return new String(response.body().bytes(), charset);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getCache(Context context, String url) throws Exception {
        return this.getCache(context, url, "utf-8");
    }

    public String getCache(Context context, String url, String charset) throws Exception {
        Request request = new Request.Builder()
                .cacheControl(new CacheControl.Builder().onlyIfCached().build()).url(url).build();
        Call call = getOkHttpClient(context).newCall(request);
        Response response = call.execute();
        if (response.code() == HTTP_STATUS_UNAVAILABLE)
            return null;
        return new String(response.body().bytes(), charset);
    }
}
