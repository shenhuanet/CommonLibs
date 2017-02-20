package com.shenhua.commonlibs.mvp;

import android.content.Context;

import com.shenhua.commonlibs.utils.NetworkUtils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
    public static final String USER_AGENT = "MQQBrowser/26 Mozilla/5.0 (Linux; U; Android 2.3.7; zh-cn; MB200 Build/GRJ22; CyanogenMod-7) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
    public static final String CONNECTION = "keep-alive";
    private Retrofit retrofit;
    private static volatile OkHttpClient okHttpClient;
    private static ExecutorService executorService;

    public static HttpManager getInstance() {
        if (instance == null) {
            instance = new HttpManager();
        }
        if (executorService == null)
            executorService = Executors.newSingleThreadExecutor();
        return instance;
    }

    public Retrofit getRetrofit(Context context, String baseUrl) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(getOkHttpClient(context))
                    //增加返回值为String的支持
                    .addConverterFactory(ScalarsConverterFactory.create())
                    //增加返回值为Gson的支持(以实体类返回)
                    .addConverterFactory(GsonConverterFactory.create())
                    //增加返回值为Oservable<T>的支持
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public OkHttpClient getOkHttpClient(Context context) {
        if (okHttpClient == null) {
            synchronized (HttpManager.class) {
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(loggingInterceptor);
                File cacheDir = new File(context.getExternalCacheDir(), "myCache");
                int cacheSize = 10 * 1024 * 1024; //10MB
                Cache cache = new Cache(cacheDir, cacheSize);
                builder.cache(cache);
                builder.addInterceptor(new RewriteCacheControlInterceptor(context));
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

    public Observable createHtmlGetObservable(final Context context, final String url) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onStart();
                Request request = new Request.Builder().url(url).build();
                Call call = getOkHttpClient(context).newCall(request);
                try {
                    Response response = call.execute();
                    subscriber.onNext(response.body().string());
                    subscriber.onCompleted();
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        });
    }

    public void sendRequest(Runnable runnable) {
        if (runnable != null)
            executorService.submit(runnable);
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
}
