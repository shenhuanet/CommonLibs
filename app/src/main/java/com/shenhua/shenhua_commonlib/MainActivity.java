package com.shenhua.shenhua_commonlib;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.shenhua.commonlibs.handler.BaseThreadHandler;
import com.shenhua.commonlibs.handler.CommonRunnable;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void request(View view) {
//        BaseThreadHandler.getInstance().sendRunnable(new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                Log.d(TAG, "run: 1");
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Log.d(TAG, "run: 2");
//                return "123456";
//            }
//        }, new BaseThreadHandler.OnUiThread<String>() {
//            @Override
//            public void onSuccess(String s) {
//                super.onSuccess(s);
//                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
//            }
//        });

//        BaseThreadHandler.getInstance().sendRunnable(new Runnable() {
//            @Override
//            public void run() {
//                Log.d(TAG, "run: 1");
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                Log.d(TAG, "run: 2");
//            }
//        }, new BaseThreadHandler.OnUiThread<Object>() {
//            @Override
//            public void onSuccess(Object o) {
//                super.onSuccess(o);
//                Log.d(TAG, "onSuccess: done2");
//                Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_SHORT).show();
//            }
//        });

        BaseThreadHandler.getInstance().sendRunnable(new CommonRunnable<String>() {
            @Override
            public String doChildThread() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return "123";
            }

            @Override
            public void doUiThread(String s) {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
