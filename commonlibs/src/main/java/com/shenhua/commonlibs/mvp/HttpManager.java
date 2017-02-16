package com.shenhua.commonlibs.mvp;

import android.content.Context;

import com.shenhua.commonlibs.utils.NetworkUtils;

import java.io.File;
import java.io.IOException;
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
import rx.Observable;
import rx.Subscriber;

/**
 * Created by Shenhua on 2/15/2017.
 * e-mail shenhuanet@126.com
 */
public class HttpManager {

    private Retrofit retrofit;
    private OkHttpClient okHttpClient;
    private Context context;

    public static HttpManager getInstance(Context context) {
        return new HttpManager(context);
    }

    private HttpManager(Context context) {
        this.context = context;
    }

    public Retrofit getRetrofit(String host) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(host)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(getOkHttpClient())
                    .build();
        }
        return retrofit;
    }

    public OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
//            if (debug) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);
//            }
            File cacheDir = new File(context.getExternalCacheDir(), "myCache");
            int cacheSize = 10 * 1024 * 1024; //10MB
            Cache cache = new Cache(cacheDir, cacheSize);
            builder.cache(cache);
            builder.addInterceptor(new RewriteCacheControlInterceptor());
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    private class RewriteCacheControlInterceptor implements Interceptor {

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

    public Observable createHtmlGetObservable(final String url) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onStart();
                Request request = new Request.Builder().url(url).build();
                Call call = getOkHttpClient().newCall(request);
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
}
